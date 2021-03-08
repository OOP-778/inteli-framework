package com.oop.inteliframework.command.bukkit;

import com.oop.inteliframework.command.ExecutorWrapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class BukkitCommandExecutor extends ExecutorWrapper {
  public final @NonNull CommandSender commandSender;

  public boolean isPlayer() {
    return commandSender instanceof Player;
  }
}
