package com.oop.inteliframework.command.error;

import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class CommandError {

  private final CommandParseHistory history;
  private final CommandElement elementCaused;
}
