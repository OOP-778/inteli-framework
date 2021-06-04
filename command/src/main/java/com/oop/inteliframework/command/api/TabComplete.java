package com.oop.inteliframework.command.api;

import com.oop.inteliframework.command.registry.parser.CommandParseHistory;

import java.util.List;

/** Used for tab completion */
@FunctionalInterface
public interface TabComplete<T extends CommandElement<T>> {

  /**
   * Complete the tab
   *
   * @return a list of suggestions
   */
  List<String> complete(T element, CommandParseHistory history);
}
