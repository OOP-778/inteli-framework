package com.oop.inteliframework.event;

import com.oop.inteliframework.event.props.AsyncHookProperties;
import com.oop.inteliframework.event.props.HookProperties;
import com.oop.inteliframework.plugin.InteliPlatform;
import lombok.NonNull;

import java.util.function.Consumer;

public class Events {
  public static void call(@NonNull Object anything) {
    InteliPlatform.getInstance()
        .safeModuleByClass(InteliEventModule.class)
        .findSystemFor(anything)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Failed to find event system for class: " + anything.getClass()))
        .call(anything);
  }

  public static <T> HookedEvent<T> hook(
      @NonNull Class<T> eventClass, HookProperties<T> properties) {
    return InteliPlatform.getInstance()
        .safeModuleByClass(InteliEventModule.class)
        .findSystemFor(eventClass)
        .orElseThrow(
            () -> new IllegalStateException("Failed to find event system for class: " + eventClass))
        .hook(eventClass, properties);
  }

  public static <T> HookedEvent<T> hook(
      @NonNull Class<T> eventClass, @NonNull Consumer<HookProperties<T>> propertiesConsumer) {
    HookProperties<T> properties = new HookProperties<>();
    propertiesConsumer.accept(properties);

    return hook(eventClass, properties);
  }

  public static <T> HookedEvent<T> hookAsync(
      @NonNull Class<T> eventClass, @NonNull Consumer<AsyncHookProperties<T>> propertiesConsumer) {
    AsyncHookProperties<T> properties = new AsyncHookProperties<>();
    propertiesConsumer.accept(properties);

    return hook(eventClass, properties);
  }

  public static class Simple {
    public static <T> void hook(@NonNull Class<T> eventClass, Consumer<T> consumer) {
      Events.hook(eventClass, props -> props.onCall((event, $) -> consumer.accept(event)));
    }
  }
}
