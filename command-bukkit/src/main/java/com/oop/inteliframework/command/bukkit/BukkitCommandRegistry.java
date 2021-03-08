package com.oop.inteliframework.command.bukkit;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.oop.inteliframework.command.registry.CommandRegistry;
import com.oop.inteliframework.commons.util.InteliVersion;
import com.oop.inteliframework.commons.util.SimpleReflection;
import com.oop.inteliframework.packetinjector.Injector;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class BukkitCommandRegistry extends CommandRegistry implements Listener {
  private static final Class<?> PACKET_PLAY_OUT_TAB_COMPLETE;
  private static final Class<?> PACKET_PLAY_IN_TAB_COMPLETE;

  static {
    try {
      PACKET_PLAY_IN_TAB_COMPLETE = SimpleReflection.findClass("{nms}.PacketPlayInTabComplete");
      PACKET_PLAY_OUT_TAB_COMPLETE = SimpleReflection.findClass("{nms}.PacketPlayOutTabComplete");
    } catch (Throwable throwable) {
      throw new IllegalStateException(
          "Failed to initialize packets for BukkitCommandRegistry", throwable);
    }
  }

  private final BukkitCommandMap commandMap;
  private final Injector injector;
  private final JavaPlugin registerer;

  public BukkitCommandRegistry(JavaPlugin plugin) {
    Bukkit.getPluginManager().registerEvents(this, plugin);
    this.registerer = plugin;
    this.commandMap = new BukkitCommandMap(this);

    injector = new Injector(plugin.getName());

    // On tab complete receive
    injector.onFiltered(
        PACKET_PLAY_IN_TAB_COMPLETE,
        (player, packet) -> {
          // Current Input
          String s = extractContent(packet);

          List<String> strings = tabComplete(new BukkitCommandExecutor(player), s);
          if (strings.isEmpty()) return true;

          Injector.PlayerConnectionHelper.sendPacket(player, constructSuggestions(s, strings));
          return false;
        });
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onCommandExecution(PlayerCommandPreprocessEvent event) {
    BukkitCommandExecutor executor = new BukkitCommandExecutor(event.getPlayer());
    String message = event.getMessage();

    if (execute(executor, message)) event.setCancelled(true);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    injector.inject(event.getPlayer());
  }

  @EventHandler
  public void onLeave(PlayerQuitEvent event) {
    injector.unregister(event.getPlayer());
  }

  @EventHandler
  public void onDisable(PluginDisableEvent event) {
    if (event.getPlugin().getName().equalsIgnoreCase(registerer.getName())) {
      injector.unregisterAll();
    }
  }

  /** We extract the current string from tab completion */
  @SneakyThrows
  private String extractContent(Object packet) {
    return (String)
        SimpleReflection.getField(PACKET_PLAY_IN_TAB_COMPLETE, String.class).get(packet);
  }

  /** Construct suggestions packet */
  @SneakyThrows
  public Object constructSuggestions(String input, List<String> suggestions) {
    if (InteliVersion.isBefore(13))
      return SimpleReflection.getConstructor(PACKET_PLAY_OUT_TAB_COMPLETE, String[].class)
          .newInstance(suggestions.toArray());

    SuggestionsBuilder suggestionsBuilder = new SuggestionsBuilder(input, input.length());
    for (String suggestion : suggestions) suggestionsBuilder.suggest(suggestion);

    return SimpleReflection.getConstructor(
            PACKET_PLAY_OUT_TAB_COMPLETE, int.class, Suggestions.class)
        .newInstance(0, suggestionsBuilder.build());
  }
}
