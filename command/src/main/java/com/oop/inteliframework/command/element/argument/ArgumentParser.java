package com.oop.inteliframework.command.element.argument;

import java.util.Queue;

@FunctionalInterface
public interface ArgumentParser<T> {
    ParseResult<T> parse(Queue<String> argsQueue);
}
