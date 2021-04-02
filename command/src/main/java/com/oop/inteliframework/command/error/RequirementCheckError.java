package com.oop.inteliframework.command.error;

import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;
import lombok.Getter;

@Getter
public class RequirementCheckError extends CommandError {

  private final CommandError filterError;

  public RequirementCheckError(
      CommandElement causer, CommandParseHistory history, CommandError filterError) {
    super(history, causer);
    this.filterError = filterError;
  }
}
