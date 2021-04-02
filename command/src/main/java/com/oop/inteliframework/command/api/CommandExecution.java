package com.oop.inteliframework.command.api;

import com.oop.inteliframework.command.CommandData;
import com.oop.inteliframework.command.ExecutorWrapper;

@FunctionalInterface
public interface CommandExecution {
  /**
   * Execution of the command
   *
   * @param executor who executed the command
   * @param commandData The data from parsing
   */
  void execute(ExecutorWrapper executor, CommandData commandData);
}
