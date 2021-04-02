package com.oop.inteliframework.command.api;

import com.oop.inteliframework.command.CommandData;
import com.oop.inteliframework.command.ExecutorWrapper;

import java.util.List;

/** Used for tab completion */
@FunctionalInterface
public interface TabComplete<T extends CommandElement<T>> {

  /**
   * Complete the tab
   *
   * @param commandData all the old matched data
   * @return a list of suggestions
   */
  List<String> complete(ExecutorWrapper executor, T element, CommandData commandData);
}
