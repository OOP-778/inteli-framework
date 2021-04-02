package com.oop.inteliframework.command;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/** Used in tab completions */
public class CommandData {
  /*
  All stored data
  */
  private final Map<String, Object> stored = new TreeMap<>(String::compareToIgnoreCase);

  /**
   * Add an object to the map
   *
   * @param object the object that we're adding
   */
  public void add(String underName, Object object) {
    stored.put(underName, object);
  }

  public boolean hasKey(String name) {
    return stored.containsKey(name);
  }

  public <T> T getAs(String name) {
    return (T) stored.get(name);
  }

  public <T> T getAs(String name, Class<T> type) {
    return (T) stored.get(name);
  }

  public <T> Optional<T> getAsOptional(String name) {
    return Optional.ofNullable((T) stored.get(name));
  }

  public <T> Optional<T> getAsOptional(String name, Class<T> type) {
    return Optional.ofNullable((T) stored.get(name));
  }

  public Set<String> keys() {
    return stored.keySet();
  }

  public void clear() {
    stored.clear();
  }
}
