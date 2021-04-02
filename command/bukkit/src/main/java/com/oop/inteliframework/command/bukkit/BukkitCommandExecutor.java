package com.oop.inteliframework.command.bukkit;

import com.oop.inteliframework.command.ExecutorWrapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.oop.inteliframework.commons.util.StringFormat.format;

@RequiredArgsConstructor
public class BukkitCommandExecutor extends ExecutorWrapper {
  public final @NonNull CommandSender commandSender;

  public boolean isPlayer() {
    return commandSender instanceof Player;
  }

  @Override
  public void sendMessage(String text, Object... args) {
    commandSender.sendMessage(format(text, args));
  }
}
