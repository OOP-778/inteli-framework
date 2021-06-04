package com.oop.inteliframework.animation.parser;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.oop.inteliframework.commons.util.Preconditions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class PrimitivesParser {
  private static final Map<Class, Function<String, Object>> parsers = new LinkedHashMap<>();

  static {
    parsers.put(Integer.class, Ints::tryParse);
    parsers.put(Double.class, Doubles::tryParse);
    parsers.put(Long.class, Longs::tryParse);
    parsers.put(Boolean.class, text -> text.equalsIgnoreCase("true"));
  }

  public static Object tryParse(String input) {
    Object parsed = null;
    System.out.println(input);
    for (Function<String, Object> parser : parsers.values()) {
      try {
        parsed = parser.apply(input);
        if (parsed != null) break;
      } catch (Throwable ignored) {
      }
    }

    Preconditions.checkArgument(parsed != null, "Unknown parser for input: " + input);
    return parsed;
  }
}
