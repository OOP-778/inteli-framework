package com.oop.inteliframework.bukkit.entity.tracker;

import com.google.common.collect.Sets;
import com.oop.inteliframework.commons.util.InteliCache;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.plugin.module.InteliModule;
import com.oop.inteliframework.task.bukkit.BukkitTaskFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Getter
public class TrackerSystem implements InteliModule {
  protected final InteliCache<String, InteliCache<InteliPair<Integer, Integer>, Set<Player>>>
      trackedPlayers =
          InteliCache.builder()
              .concurrencyLevel(1)
              .resetExpireAfterAccess(true)
              .expireAfter(10, TimeUnit.SECONDS)
              .build();
  private final String name;

  @Setter private long updateEvery = 4;
  private long _nextUpdate = -1;

  public synchronized void track(String world, InteliPair<Integer, Integer>... chunks) {
    InteliCache<InteliPair<Integer, Integer>, Set<Player>> chunksCache =
        trackedPlayers.getIfAbsent(
            world,
            () ->
                InteliCache.builder()
                    .concurrencyLevel(1)
                    .expireAfter(10, TimeUnit.SECONDS)
                    .resetExpireAfterAccess(true)
                    .build());
    if (chunksCache.keys().containsAll(Arrays.asList(chunks))) return;

    for (InteliPair<Integer, Integer> chunk : chunks)
      if (!chunksCache.has(chunk)) chunksCache.put(chunk, new HashSet<>());
  }

  public synchronized Set<Player> request(String world, InteliPair<Integer, Integer>... chunks) {
    Set<Player> players = new HashSet<>();

    trackedPlayers
        .get(world)
        .ifPresent(
            worldPlayers -> {
              for (InteliPair<Integer, Integer> chunk : chunks) {
                worldPlayers.get(chunk).ifPresent(players::addAll);
              }
            });

    return players;
  }

  public void update() {
    if (_nextUpdate == -1) {
      _nextUpdate = updateEvery;
      return;
    }

    _nextUpdate--;
    if (_nextUpdate != 0) {
      return;
    }

    _nextUpdate = updateEvery;
    BukkitTaskFactory.enforceSync(
        () -> {
          // Get worlds by requested worlds
          final Map<String, List<Player>> worldsPlayers = new HashMap<>();
          for (String key : trackedPlayers.keys())
            worldsPlayers.put(key, Bukkit.getWorld(key).getPlayers());

          platform()
              .safeModuleByClass(InteliPlayerTracker.class)
              .getTaskController()
              .prepareTask(
                  task -> {
                    task.body(
                        $ -> {
                          for (String world : trackedPlayers.keys()) {
                            InteliCache<InteliPair<Integer, Integer>, Set<Player>> chunksCache =
                                trackedPlayers.get(world).orElse(null);

                            // If expired continue
                            if (chunksCache == null) continue;

                            Set<Player> worldPlayers =
                                Sets.newConcurrentHashSet(worldsPlayers.get(world));
                            for (InteliPair<Integer, Integer> chunkCoords : chunksCache.keys()) {
                              Set<Player> players = chunksCache.get(chunkCoords).orElse(null);

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
                                if (chunkCoords.getKey() != chunkX
                                    || chunkZ != chunkCoords.getValue()) continue;

                                players.add(worldPlayer);
                                worldPlayers.remove(worldPlayer);
                              }

                              players.removeIf(
                                  player -> {
                                    Location location = player.getLocation();
                                    int chunkX = location.getBlockX() >> 4;
                                    int chunkZ = location.getBlockZ() >> 4;

                                    return chunkCoords.getKey() != chunkX
                                        || chunkZ != chunkCoords.getValue();
                                  });
                            }
                          }
                        });
                    task.run();
                  });
        });
  }
}
