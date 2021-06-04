package com.oop.inteliframework.entity.hologram;

import com.google.common.collect.Sets;
import com.oop.inteliframework.bukkit.entity.tracker.InteliPlayerTracker;
import com.oop.inteliframework.bukkit.entity.tracker.TrackerSystem;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.plugin.module.InteliModule;
import com.oop.inteliframework.task.api.Task;
import com.oop.inteliframework.task.api.TaskController;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InteliHologramFactory implements InteliModule {

  protected final TaskController<?, Task> taskController;
  @Getter private final TrackerSystem tracker;
  protected Map<String, Map<InteliPair<Integer, Integer>, Set<Hologram>>> registry =
      new ConcurrentHashMap<>();

  public InteliHologramFactory(String name, TaskController taskController) {
    this.taskController = taskController;

    this.tracker =
        platform()
            .safeModuleByClass(InteliPlayerTracker.class)
            .getOrCreate(name, () -> new TrackerSystem(name));
  }

  public InteliHologramFactory(TaskController taskController) {
    this("inteli-hologram-system", taskController);
  }

  public void registerHologram(Hologram hologram) {
    Location baseLocation =
        Objects.requireNonNull(
            hologram.getLocation(), "Failed to register hologram, cause location is not assigned!");
    int chunkX = baseLocation.getBlockX() >> 4;
    int chunkZ = baseLocation.getBlockZ() >> 4;

    Map<InteliPair<Integer, Integer>, Set<Hologram>> worldHolograms =
        registry.computeIfAbsent(
            baseLocation.getWorld().getName(), name -> new ConcurrentHashMap<>());

    hologram.factory = this;
    if (hologram.task != null) {
      hologram.task.cancel();
    }

    hologram.task =
        taskController.prepareTask(
            task -> {
              task.repeatable(true);
              task.delay(1);
              task.body(
                  $ -> {
                    try {
                      hologram.update();
                    } catch (Throwable throwable) {
                      logger().error("An error was thrown in midst of updating an hologram");
                      throwable.printStackTrace();
                    }
                  });
              task.run();
            });

    Set<Hologram> hologramViews =
        worldHolograms.computeIfAbsent(
            new InteliPair<>(chunkX, chunkZ), pair -> Sets.newConcurrentHashSet());
    hologramViews.add(hologram);
  }

  public void unregister(Hologram hologram) {
    Optional.ofNullable(registry.get(hologram.getLocation().getWorld().getName()))
        .map(
            map ->
                map.get(
                    new InteliPair<>(
                        hologram.getLocation().getBlockX() >> 4,
                        hologram.getLocation().getBlockZ() >> 4)))
        .ifPresent(holograms -> holograms.remove(hologram));
  }

  public Set<Hologram> getHolograms(int chunkX, int chunkZ, String world, Player player) {
    final Map<InteliPair<Integer, Integer>, Set<Hologram>> worldHolograms = registry.get(world);
    if (worldHolograms == null) return new HashSet<>();

    final Set<Hologram> hologramViews = worldHolograms.get(new InteliPair<>(chunkX, chunkZ));
    if (hologramViews == null) return new HashSet<>();

    return hologramViews.stream().filter(hologram -> player == null).collect(Collectors.toSet());
  }
}
