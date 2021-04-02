package com.oop.inteliframework.command.registry.parser;

import com.oop.inteliframework.command.api.CommandElement;

@FunctionalInterface
public interface ElementParser<T extends CommandElement> {

  CommandElement parse(
      T element, CommandElement<?> parent, String peek, CommandParseHistory history);
}
