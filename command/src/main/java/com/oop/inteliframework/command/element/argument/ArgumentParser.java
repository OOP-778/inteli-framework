package com.oop.inteliframework.command.element.argument;

import com.oop.inteliframework.command.registry.parser.CommandParseHistory;

import java.util.Queue;

@FunctionalInterface
public interface ArgumentParser<T> {
  ParseResult<T> parse(Queue<String> argsQueue, CommandParseHistory history);
}
