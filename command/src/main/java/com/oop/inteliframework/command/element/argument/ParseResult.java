package com.oop.inteliframework.command.element.argument;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ParseResult<T> {

  private final String message;
  private final T object;

  public ParseResult(T object, String message) {
    this.message = message;
    this.object = object;
  }

  public ParseResult(@NonNull T object) {
    this(object, null);
  }

  public ParseResult(@NonNull String message) {
    this(null, message);
  }
}
