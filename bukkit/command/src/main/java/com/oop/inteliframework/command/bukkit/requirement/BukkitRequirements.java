package com.oop.inteliframework.command.bukkit.requirement;

import com.oop.inteliframework.command.api.requirement.CommandElementRequirement;
import com.oop.inteliframework.command.bukkit.BukkitCommandExecutor;
import com.oop.inteliframework.command.bukkit.requirement.error.CommandPermissionNotMetError;

public interface BukkitRequirements {

  static CommandElementRequirement permission(String permission) {
    return (element, history) -> {
      boolean hasPermission =
          history
              .getExecutor()
              .as(BukkitCommandExecutor.class)
              .commandSender
              .hasPermission(permission);
      if (hasPermission) {
        return null;
      }

      return new CommandPermissionNotMetError(history, element, permission);
    };
  }
}
