package com.oop.inteliframework.message;

import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.function.Function;

public class Replacer {
  private final Set<Function<String, String>> replacers = new HashSet<>();

  private static String _replaceIgnoreCase(String source, String target, String replacement) {
    StringBuilder sbSource = new StringBuilder(source);
    StringBuilder sbSourceLower = new StringBuilder(source.toLowerCase());
    String searchString = target.toLowerCase();

    int idx = 0;
    while ((idx = sbSourceLower.indexOf(searchString, idx)) != -1) {
      sbSource.replace(idx, idx + searchString.length(), replacement);
      sbSourceLower.replace(idx, idx + searchString.length(), replacement);
      idx += replacement.length();
    }
    sbSourceLower.setLength(0);
    sbSourceLower.trimToSize();

    return sbSource.toString();
  }

  public Replacer replaceLiteral(String key, String value) {
    return replaceLiteral(key, value, false);
  }

  public Replacer replaceLiteral(String key, String value, boolean ignoreCase) {
    replacers.add(
        in -> {
          if (!ignoreCase) {
            return StringUtils.replace(in, key, value);
          }

          return _replaceIgnoreCase(in, key, value);
        });
    return this;
  }

  public Replacer replaceFromMap(Map<String, String> map) {
    replacers.add(
        in -> {
          for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            in = StringUtils.replace(in, stringStringEntry.getKey(), stringStringEntry.getValue());
          }
          return in;
        });
    return this;
  }

  public String accept(String text) {
    for (Function<String, String> replacer : replacers) {
      text = replacer.apply(text);
    }

    return text;
  }

  public List<String> accept(Collection<String> collection) {
    final List<String> replacedList = new LinkedList<>();
    for (String line : collection) {
        replacedList.add(accept(line));
    }

    return replacedList;
  }
}
