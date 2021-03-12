package com.oop.inteliframework.command.bukkit;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.command.registry.CommandRegistry;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.commons.util.InteliVersion;
import com.oop.inteliframework.commons.util.SimpleReflection;
import com.oop.inteliframework.packetinjector.Injector;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
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
          String content = extractContent(packet);
          if (!content.startsWith("/")) return true;

          List<String> strings = tabComplete(new BukkitCommandExecutor(player), content);
          if (strings.isEmpty()) return true;

          Injector.PlayerConnectionHelper.sendPacket(player, constructSuggestions(content, strings));
          return false;
        });

    // If plugin has been reloaded, hook into players rn
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      injector.inject(onlinePlayer);
    }
  }

  @Override
  public void register(Command command) {
    super.register(command);
    commandMap.register(command);
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
      // Remove all packet listeners
      injector.unregisterAll();

      // Remove all registered commands
      commandMap.unregisterAll();
    }
  }

  /** We extract the current string from tab completion */
  @SneakyThrows
  private String extractContent(Object packet) {
    return (String) SimpleReflection.getField(PACKET_PLAY_IN_TAB_COMPLETE, String.class).get(packet);
  }

  /** Construct suggestions packet */
  @SneakyThrows
  public Object constructSuggestions(String input, List<String> suggestions) {
    if (InteliVersion.isBefore(13)) {
      return SimpleReflection
              .getConstructor(PACKET_PLAY_OUT_TAB_COMPLETE, String[].class)
              .newInstance((Object) suggestions.toArray(new String[0]));
    }

    SuggestionsBuilder suggestionsBuilder = new SuggestionsBuilder(input, input.length());
    for (String suggestion : suggestions) suggestionsBuilder.suggest(suggestion);

    return SimpleReflection.getConstructor(
            PACKET_PLAY_OUT_TAB_COMPLETE, int.class, Suggestions.class)
        .newInstance(0, suggestionsBuilder.build());
  }
}
