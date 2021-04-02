package com.oop.inteliframework.command.api.requirement;

import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.error.CommandError;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;
import lombok.NonNull;

@FunctionalInterface
public interface CommandElementRequirement {
  CommandError apply(@NonNull CommandElement element, CommandParseHistory history);
}
