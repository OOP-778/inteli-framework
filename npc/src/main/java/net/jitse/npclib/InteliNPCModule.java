/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib;

import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.Getter;
import net.jitse.npclib.interact.PlayerInteractHandler;
import net.jitse.npclib.listeners.ChunkListener;
import net.jitse.npclib.listeners.PlayerListener;
import net.jitse.npclib.listeners.PlayerMoveEventListener;
import net.jitse.npclib.player.PlayerTracker;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InteliNPCModule implements InteliModule {
  private static final int CHUNK_VIEW_DISTANCE = 3;
  private final Class<?> npcClass;
  private final JavaPlugin plugin = (JavaPlugin) InteliPlatform.getInstance().starter();

  @Getter private final Map<UUID, PlayerTracker> trackerMap = new ConcurrentHashMap<>();

  @Getter private final NPCHolder npcHolder = new NPCHolder();

  private InteliNPCModule() {
    String versionName = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
    Class<?> npcClass = null;

    try {
      npcClass = Class.forName("net.jitse.npclib.nms." + versionName + ".NPC_" + versionName);
    } catch (ClassNotFoundException exception) {
      // Version not supported, error below.
    }

    this.npcClass = npcClass;

    if (npcClass == null) {
      logger()
          .error("Failed to initiate. Your server's version ({}) is not supported", versionName);
      return;
    }

    new PlayerListener();
    new ChunkListener();
    new PlayerMoveEventListener();

    new PlayerInteractHandler();
    logger().info("Enabled for Minecraft {}", versionName);
  }

  public PlayerTracker trackerFor(Player player) {
    return trackerMap.computeIfAbsent(
        player.getUniqueId(), $ -> new PlayerTracker(player, CHUNK_VIEW_DISTANCE));
  }
}
