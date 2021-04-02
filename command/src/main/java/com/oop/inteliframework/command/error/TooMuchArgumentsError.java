package com.oop.inteliframework.command.error;

import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;

public class TooMuchArgumentsError extends CommandError {
  public TooMuchArgumentsError(CommandParseHistory history, CommandElement elementCaused) {
    super(history, elementCaused);
  }
}
