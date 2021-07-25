package com.oop.inteliframework.command.bungee;

import com.oop.inteliframework.command.ExecutorWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;

import static com.oop.inteliframework.commons.util.StringFormat.format;

@AllArgsConstructor
public class BungeeCommandExecutor extends ExecutorWrapper {

  @Getter
  private final CommandSender sender;

  @Override
  public void sendMessage(String text, Object... args) {
    sender.sendMessage(format(text, args));
  }

  @Override
  public boolean isConsole() {
    return sender instanceof ConsoleCommandSender;
  }

  public ProxiedPlayer asPlayer() {
      return (ProxiedPlayer) sender;
  }
}
