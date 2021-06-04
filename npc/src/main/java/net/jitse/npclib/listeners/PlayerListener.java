/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib.listeners;

import com.oop.inteliframework.event.Events;
import com.oop.inteliframework.task.SimpleTaskFactory;
import net.jitse.npclib.player.PlayerHandler;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.TimeUnit;

/** @author Jitse Boonstra */
public class PlayerListener implements Listener {

  public PlayerListener() {
    Events.Simple.hook(
        PlayerQuitEvent.class,
        event -> {
          PlayerHandler.onLeave(event.getPlayer());
        });

    Events.Simple.hook(
        PlayerTeleportEvent.class,
        event -> {
          PlayerHandler.onChunkChange(event.getPlayer());
        });

    Events.Simple.hook(
        PlayerRespawnEvent.class,
        event -> {
          Player player = event.getPlayer();
          World world = player.getWorld();

          SimpleTaskFactory.later(
                  $ -> {
                    if (world.equals(player.getWorld())) {
                      PlayerHandler.onChunkChange(player);
                      return;
                    }

                    PlayerHandler.onWorldChange(player);
                  },
                  100,
                  TimeUnit.MILLISECONDS)
              .run();
        });

    Events.Simple.hook(
        PlayerChangedWorldEvent.class,
        event -> {
          Player player = event.getPlayer();
          PlayerHandler.onWorldChange(player);
        });
  }
}
