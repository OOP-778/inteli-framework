package com.oop.inteliframework.hologram;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.hologram.animation.AnimationProvider;
import com.oop.inteliframework.hologram.animation.ContentAnimation;
import com.oop.inteliframework.hologram.nms.WrappedArmorStand;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HologramController {
    private static final Map<String, HologramController> controllerMap = new ConcurrentHashMap<>();
    public static Logger LOGGER = Logger.getLogger("InteliHolograms");
    private final TreeMap<String, AnimationProvider> animationProviders = new TreeMap<>(Maps.newConcurrentMap());
    private ScheduledExecutorService executor;
    private JavaPlugin plugin;
    private Map<String, Map<InteliPair<Integer, Integer>, Set<Hologram>>> holograms = new ConcurrentHashMap<>();
    private List<Hologram> cache;
    private volatile boolean updated = true;

    private HologramController(
            JavaPlugin plugin,
            ScheduledExecutorService executor
    ) {
        if (controllerMap.containsKey(plugin.getName()))
            throw new IllegalStateException("Plugin already has registered a controller! Please use #getFor(JavaPlugin)");

        this.executor = executor;
        controllerMap.put(plugin.getName(), this);
        this.plugin = plugin;

        // Initialize Player Tracker
        PlayerTracker.register(plugin);
    }

    public static Builder builder() {
        return new Builder();
    }

    public void registerHologram(Hologram hologram) {
        Location baseLocation = Objects.requireNonNull(hologram.getLocation(), "Failed to register hologram, cause location is not assigned!");
        int chunkX = baseLocation.getBlockX() >> 4;
        int chunkZ = baseLocation.getBlockZ() >> 4;

        Map<InteliPair<Integer, Integer>, Set<Hologram>> worldHolograms =
                holograms.computeIfAbsent(baseLocation.getWorld().getName(), name -> new ConcurrentHashMap<>());

        Set<Hologram> hologramViews = worldHolograms.computeIfAbsent(new InteliPair<>(chunkX, chunkZ), pair -> Sets.newConcurrentHashSet());
        hologramViews.add(hologram);
        updated = true;

        hologram.setUpdateTask(executor.scheduleAtFixedRate(() -> {
            try {
                hologram.update();
            } catch (Throwable throwable) {
                new IllegalStateException("Failed to update hologram at location " + hologram.getLocation(), throwable).printStackTrace();
            }
        }, 1L, 1L, TimeUnit.MILLISECONDS));
    }

    public Set<Hologram> getHolograms(int chunkX, int chunkZ, String world, Player player) {
        Map<InteliPair<Integer, Integer>, Set<Hologram>> worldHolograms = holograms.get(world);
        if (worldHolograms == null) return new HashSet<>();

        Set<Hologram> hologramViews = worldHolograms.get(new InteliPair<>(chunkX, chunkZ));
        if (hologramViews == null) return new HashSet<>();

        return hologramViews
                .stream()
                .filter(hologram -> player == null)
                .collect(Collectors.toSet());
    }

    public InteliPair<String, List<ContentAnimation>> initAnimations(String text) {
        return new InteliPair<>(text, new ArrayList<>());
    }

    @SneakyThrows
    public void onDisable() {
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        for (Map<InteliPair<Integer, Integer>, Set<Hologram>> value : holograms.values())
            value.values().stream()
                    .flatMap(Collection::stream)
                    .forEach(Hologram::remove);

        controllerMap.remove(plugin.getName());
    }

    public boolean setupNms() {
        LOGGER.info("Initializing NMS...");
        boolean armorstandFound = WrappedArmorStand.supplier != null;

        LOGGER.info("ArmorStand Wrapper: " + (armorstandFound ? "FOUND" : "MISSING"));
        return armorstandFound;
    }

    @Accessors(chain = true, fluent = true)
    @Setter
    public static class Builder {

        private @NonNull JavaPlugin plugin;
        private ScheduledExecutorService executorService;

        private Builder() {
        }

        public HologramController build() {

            if (executorService == null)
                executorService = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread inteliHolograms = new Thread(r, "InteliHolograms");
                    inteliHolograms.setUncaughtExceptionHandler((err, th) -> th.printStackTrace());
                    return inteliHolograms;
                });

            HologramController controller = new HologramController(plugin, executorService);
            controller.setupNms();
            return controller;
        }
    }
}
