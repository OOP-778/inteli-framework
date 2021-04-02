package com.oop.inteliframework.command.error;

import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.element.argument.ParseResult;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;
import lombok.Getter;

@Getter
public class InvalidArgumentError extends CommandError {

  private final ParseResult result;

  public InvalidArgumentError(
      CommandElement causer, CommandParseHistory history, ParseResult result) {
    super(history, causer);
    this.result = result;
  }
}
