package com.oop.inteliframework.event;

import com.oop.inteliframework.plugin.module.InteliModule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class InteliEventModule implements InteliModule {
  private final Set<EventSystem<?>> registeredSystems = new HashSet<>();

  public void registerSystem(EventSystem<?>... system) {
    registeredSystems.addAll(Arrays.asList(system));
  }

  public <T> Optional<EventSystem<T>> findSystemFor(Class<T> clazz) {
    for (EventSystem<?> registeredSystem : registeredSystems) {
      if (!registeredSystem.accepts(clazz)) continue;

      return Optional.of((EventSystem<T>) registeredSystem);
    }

    return Optional.empty();
  }

  public <T> Optional<EventSystem<T>> findSystemFor(T object) {
    for (EventSystem<?> registeredSystem : registeredSystems) {
      if (!registeredSystem.accepts(object.getClass())) continue;

      return Optional.of((EventSystem<T>) registeredSystem);
    }

    return Optional.empty();
  }
}
