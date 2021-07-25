package com.oop.inteliframework.command.element.argument;

import com.google.common.primitives.Doubles;

import java.util.function.Consumer;

import static com.oop.inteliframework.commons.util.StringFormat.format;

/** Default implemented arguments for primitive types */
public interface Arguments {
  @SafeVarargs
  static Argument<Number> numberArg(Consumer<Argument<Number>>... consumer) {
    Argument<Number> arg = new Argument<>();
    arg.labeled("number");
    arg.parser(
        (inputs, $) -> {
          String input = inputs.poll();
          Double parsed = Doubles.tryParse(input);
          if (parsed == null) return new ParseResult<>(format("Invalid Number: {}", input));

          return new ParseResult<>(parsed);
        });

    if (consumer.length != 0) {
      consumer[0].accept(arg);
    }

    return arg;
  }
}
