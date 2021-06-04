package com.oop.inteliframework.event;

import com.oop.inteliframework.event.props.HookProperties;
import com.oop.inteliframework.plugin.module.InteliModule;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EventSystem<T> implements InteliModule {

  protected final Map<HookProperties.Priority, Map<Class<? super T>, Set<HookedEvent<? super T>>>>
      hooked = new ConcurrentHashMap<>();

  private final Class<T> baseEventClass;

  public EventSystem(Class<T> baseEventClass) {
    this.baseEventClass = baseEventClass;
  }

  public boolean accepts(Class<?> clazz) {
    return baseEventClass.isAssignableFrom(clazz);
  }

  public abstract void call(T event);

  protected <E extends T> Set<HookedEvent<? super T>> getHookedEvents(
      HookProperties.Priority priority, Class<E> eventClass) {
    final Map<Class<? super T>, Set<HookedEvent<? super T>>> classSetMap =
        hooked.computeIfAbsent(priority, $ -> new ConcurrentHashMap<>());
    final Set<HookedEvent<? super T>> hookedEvents =
        classSetMap.computeIfAbsent(
            (Class<? super T>) eventClass, $ -> ConcurrentHashMap.newKeySet());
    return hookedEvents;
  }

  public <E extends T> HookedEvent<E> hook(Class<E> eventClass, HookProperties<E> properties) {
    final Set<HookedEvent<? super T>> hookedEvents =
        getHookedEvents(properties.priority(), eventClass);

    HookedEvent<E> event = new HookedEvent<>(properties);
    event.unhook = () -> hookedEvents.remove(event);

    hookedEvents.add((HookedEvent<? super T>) event);
    return event;
  }
}
