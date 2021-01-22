package com.oop.inteliframework.hologram;

import com.google.common.collect.Sets;
import com.oop.inteliframework.commons.util.InteliCache;
import com.oop.inteliframework.commons.util.InteliPair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PlayerTracker {
    private static PlayerTracker tracker;
    private InteliCache<String, InteliCache<InteliPair<Integer, Integer>, Set<Player>>> trackedPlayers = InteliCache.builder()
            .concurrencyLevel(1)
            .resetExpireAfterAccess(true)
            .expireAfter(10, TimeUnit.SECONDS)
            .build();

    private JavaPlugin registerer;

    // This will be registered by who the first registers
    private PlayerTracker() {}

    private Runnable execute() {
        return () -> {
            // Get worlds by requested worlds
            Map<String, List<Player>> worldsPlayers = new HashMap<>();
            for (String key : trackedPlayers.keys())
                worldsPlayers.put(key, Bukkit.getWorld(key).getPlayers());

            // Process players asynchronous
            async(() -> {
                for (String world : trackedPlayers.keys()) {
                    InteliCache<InteliPair<Integer, Integer>, Set<Player>> chunksCache = trackedPlayers.get(world);

                    // If expired continue
                    if (chunksCache == null) continue;

                    Set<Player> worldPlayers = Sets.newConcurrentHashSet(worldsPlayers.get(world));
                    for (InteliPair<Integer, Integer> chunkCoords : chunksCache.keys()) {
                        Set<Player> players = chunksCache.get(chunkCoords);

                        // If expired continue
                        if (players == null) continue;

                        for (Player worldPlayer : worldPlayers) {
                            if (!worldPlayer.isOnline()) {
                                worldPlayers.remove(worldPlayer);
                                continue;
                            }

                            Location location = worldPlayer.getLocation();
                            int chunkX = location.getBlockX() >> 4;
                            int chunkZ = location.getBlockZ() >> 4;
                            if (chunkCoords.getKey() != chunkX || chunkZ != chunkCoords.getValue())
                                continue;

                            players.add(worldPlayer);
                            worldPlayers.remove(worldPlayer);
                        }

                        players.removeIf(player -> {
                            Location location = player.getLocation();
                            int chunkX = location.getBlockX() >> 4;
                            int chunkZ = location.getBlockZ() >> 4;

                            return chunkCoords.getKey() != chunkX || chunkZ != chunkCoords.getValue();
                        });
                    }
                }
            });
        };
    }

    public static void register(JavaPlugin plugin) {
        if (tracker != null) return;

        tracker = new PlayerTracker();
        tracker.registerer = plugin;
        Bukkit.getScheduler().runTaskTimer(plugin, tracker.execute(), 20 * 2, 20 * 2);
    }

    private void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(registerer, runnable);
    }

    public static synchronized void track(String world, InteliPair<Integer, Integer>... chunks) {
        InteliCache<InteliPair<Integer, Integer>, Set<Player>> chunksCache = tracker.trackedPlayers.getIfAbsent(world, () -> InteliCache.builder().concurrencyLevel(1).expireAfter(10, TimeUnit.SECONDS).resetExpireAfterAccess(true).build());
        if (chunksCache.keys().containsAll(Arrays.asList(chunks)))
            return;

        for (InteliPair<Integer, Integer> chunk : chunks)
            if (!chunksCache.has(chunk)) chunksCache.put(chunk, new HashSet<>());
    }

    public static synchronized Set<Player> request(String world, InteliPair<Integer, Integer>... chunks) {
        Set<Player> players = new HashSet<>();

        InteliCache<InteliPair<Integer, Integer>, Set<Player>> worldPlayers = tracker.trackedPlayers.get(world);
        if (worldPlayers == null) return players;

        for (InteliPair<Integer, Integer> chunk : chunks) {
            Optional
                    .ofNullable(worldPlayers.get(chunk))
                    .ifPresent(players::addAll);
        }

        return players;
    }
}
