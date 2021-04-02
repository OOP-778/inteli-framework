package com.oop.inteliframework.command.style;

import com.oop.inteliframework.command.error.CommandError;
import com.oop.inteliframework.command.registry.CommandRegistry;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;
import lombok.NonNull;

public interface RegistryStyle {
  void handleError(
      @NonNull CommandError[] errors,
      @NonNull CommandRegistry registry,
      @NonNull CommandParseHistory history);
}
