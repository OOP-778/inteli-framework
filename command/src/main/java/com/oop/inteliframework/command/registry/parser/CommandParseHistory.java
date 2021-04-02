package com.oop.inteliframework.command.registry.parser;

import com.oop.inteliframework.command.CommandData;
import com.oop.inteliframework.command.ExecutorWrapper;
import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.error.CommandError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Queue;
import java.util.Set;

@Data
@AllArgsConstructor
@ToString
public class CommandParseHistory {

  private final CommandData data;
  private final ExecutorWrapper executor;
  private final Queue<String> waitingForParse;
  private final List<CommandError> resultedInto;
  private final Set<CommandElement> path;
  private CommandElement<?> lastElement;
}
