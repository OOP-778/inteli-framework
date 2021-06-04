package net.jitse.npclib.listeners;

import com.oop.inteliframework.event.Events;
import net.jitse.npclib.player.PlayerHandler;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveEventListener {
  public PlayerMoveEventListener() {
    Events.Simple.hook(
        PlayerMoveEvent.class,
        event -> {
          Location from = event.getFrom();
          Location to = event.getTo();
          if (to.getChunk().equals(from.getChunk())) return;

          PlayerHandler.onChunkChange(event.getPlayer());
        });
  }
}
