package com.oop.inteliframework.command.bungee.requirement;

import com.oop.inteliframework.command.api.requirement.CommandElementRequirement;
import com.oop.inteliframework.command.bungee.BungeeCommandExecutor;
import com.oop.inteliframework.command.error.MessageCommandError;

public interface BungeeRequirements {

  static CommandElementRequirement permission(String permission) {
    return (element, history) -> {
      boolean hasPermission =
          history
              .getExecutor()
              .as(BungeeCommandExecutor.class)
              .getSender()
              .hasPermission(permission);
      if (hasPermission) {
        return null;
      }

      return new MessageCommandError(history, element, "You don't have permission: " + permission);
    };
  }
}
