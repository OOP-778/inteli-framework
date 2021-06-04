/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib.listeners;

import com.oop.inteliframework.event.Events;
import com.oop.inteliframework.plugin.InteliPlatform;
import net.jitse.npclib.InteliNPCModule;
import net.jitse.npclib.internal.NPCBase;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Objects;
import java.util.UUID;

public class ChunkListener {

  public ChunkListener() {
    Events.hook(
        ChunkUnloadEvent.class,
        props -> {
          props.onCall(
              (event, $) -> {
                Chunk chunk = event.getChunk();

                for (NPCBase npc :
                    InteliPlatform.getInstance()
                        .safeModuleByClass(InteliNPCModule.class)
                        .getNpcHolder()
                        .chunkNPCS(event.getWorld().getName(), chunk.getX(), chunk.getZ())
                        .values()) {

                  // We found an NPC in the chunk being unloaded. Time to hide this NPC from all
                  // players.
                  for (UUID uuid : npc.getShown()) {
                    // Safety check so it doesn't send packets if the NPC has already
                    // been automatically despawned by the system.
                    if (npc.getAutoHidden().contains(uuid)) {
                      continue;
                    }

                    // Bukkit.getPlayer(uuid) sometimes returns null
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                      npc.hide(player, true);
                    }
                  }
                }
              });
        });

    Events.hook(
        ChunkLoadEvent.class,
        props -> {
          props.onCall(
              (event, $) -> {
                Chunk chunk = event.getChunk();
                for (NPCBase npc :
                    InteliPlatform.getInstance()
                        .safeModuleByClass(InteliNPCModule.class)
                        .getNpcHolder()
                        .chunkNPCS(event.getWorld().getName(), chunk.getX(), chunk.getZ())
                        .values()) {
                  // The chunk being loaded has this NPC in it. Showing it to all the players again.
                  for (UUID uuid : npc.getShown()) {
                    // Make sure not to respawn a not-hidden NPC.
                    if (!npc.getAutoHidden().contains(uuid)) {
                      continue;
                    }

                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) continue;

                    if (!Objects.equals(npc.getWorld(), player.getWorld())) {
                      continue;
                    }

                    npc.qualify(player);
                  }
                }
              });
        });
  }
}
