package com.oop.inteliframework.command.bungee;

import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.command.registry.CommandRegistry;
import com.oop.inteliframework.event.Events;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;

public class BungeeCommandRegistry extends CommandRegistry {
  public BungeeCommandRegistry() {
    Events.Simple.hook(
        ChatEvent.class,
        event -> {
          BungeeCommandExecutor executor =
              new BungeeCommandExecutor((CommandSender) event.getSender());
          if (!execute(executor, event.getMessage())) return;

          event.setCancelled(true);
        });

    Events.Simple.hook(
        TabCompleteEvent.class,
        event -> {
          BungeeCommandExecutor executor =
              new BungeeCommandExecutor((CommandSender) event.getSender());
          List<String> result = tabComplete(executor, event.getCursor());
          event.getSuggestions().addAll(result);
        });
  }

  @Override
  public void register(Command command) {
    super.register(command);

    final BungeeCommandRegistry registry = this;

    final net.md_5.bungee.api.plugin.Command bungeeCommand =
        new net.md_5.bungee.api.plugin.Command(
            command.labeled(), "", command.aliases().toArray(new String[0])) {
          @Override
          public void execute(CommandSender commandSender, String[] strings) {
            final BungeeCommandExecutor executor = new BungeeCommandExecutor(commandSender);
            registry.execute(executor, command.labeled() + " " + String.join(" ", strings));
          }
        };

    ProxyServer.getInstance()
        .getPluginManager()
        .registerCommand(((Plugin) platform().starter()), bungeeCommand);
  }
}
