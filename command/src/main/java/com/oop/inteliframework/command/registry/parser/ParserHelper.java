package com.oop.inteliframework.command.registry.parser;

import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.api.requirement.CommandElementRequirement;
import com.oop.inteliframework.command.error.CommandError;
import com.oop.inteliframework.command.error.RequirementCheckError;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ParserHelper {

  @Nullable
  public static CommandError checkRequirements(
      @NonNull CommandElement<?> element, @NonNull CommandParseHistory history) {
    Map<String, CommandElementRequirement> values = element.requirements().values();
    if (values.isEmpty()) return null;

    for (Map.Entry<String, CommandElementRequirement> filterEntry : values.entrySet()) {

      CommandError apply = filterEntry.getValue().apply(element, history);
      if (apply == null) continue;

      return new RequirementCheckError(element, history, apply);
    }

    return null;
  }
}
