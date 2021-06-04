package net.jitse.npclib.interact;

import com.oop.inteliframework.event.Events;
import com.oop.inteliframework.packetinjector.Injector;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerInteractHandler extends Injector {
  public PlayerInteractHandler() {
    super("npc-packet-handler");

    Events.Simple.hook(
        PlayerJoinEvent.class,
        event -> {
          inject(event.getPlayer());
        });

    Events.Simple.hook(
        PlayerQuitEvent.class,
        event -> {
          unregister(event.getPlayer());
        });
  }
}
