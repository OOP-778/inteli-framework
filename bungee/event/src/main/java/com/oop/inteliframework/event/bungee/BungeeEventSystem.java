package com.oop.inteliframework.event.bungee;

import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.commons.util.SimpleReflection;
import com.oop.inteliframework.event.EventSystem;
import com.oop.inteliframework.event.HookedEvent;
import com.oop.inteliframework.event.props.HookProperties;
import lombok.SneakyThrows;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventBus;
import net.md_5.bungee.event.EventPriority;

import java.lang.reflect.Method;
import java.util.*;

public class BungeeEventSystem extends EventSystem<Event> {
  public BungeeEventSystem() {
    super(Event.class);

    platform()
        .starter()
        .hookDisable(
            () -> {
              for (Map<Class<? super Event>, Set<HookedEvent<? super Event>>> value :
                  hooked.values()) {
                for (Set<HookedEvent<? super Event>> hookedEvents : value.values()) {
                  for (HookedEvent<? super Event> hookedEvent : hookedEvents) {
                    hookedEvent.unhook();
                  }
                }
              }
            });
  }

  @Override
  public void call(Event event) {
    ProxyServer.getInstance().getPluginManager().callEvent(event);
  }

  @Override
  public <E extends Event> HookedEvent<E> hook(Class<E> eventClass, HookProperties<E> properties) {
    Set<HookedEvent<? super Event>> hookedEvents =
        getHookedEvents(properties.priority(), eventClass);

    final byte bungeePriority = hookPriorityToEvent(properties.priority());
    BungeeHookedEvent eBungeeHookedEvent = new BungeeHookedEvent<>(properties);
    hookedEvents.add(eBungeeHookedEvent);

    final BungeeEventHandler<E> bungeeEventHandler = new BungeeEventHandler<E>() {
      @Override
      public void handle(E event) {
        eBungeeHookedEvent.handle(event);
      }
    };

    eBungeeHookedEvent.setEventHandler(bungeeEventHandler);

    Runnable unregister = registerListener(eventClass, bungeeEventHandler, bungeePriority);
    eBungeeHookedEvent.setUnhook(unregister);

    return eBungeeHookedEvent;
  }

  protected  <E extends Event> Method findDeclaredMethod(BungeeEventHandler<E> handler, Class<E> clazz) {
    for (Method declaredMethod : handler.getClass().getDeclaredMethods()) {
      if (declaredMethod.getParameterTypes()[0].equals(Event.class)) {
        declaredMethod.setAccessible(true);
        return declaredMethod;
      }
    }

    return null;
  }

  @SneakyThrows
  protected <E extends Event> Runnable registerListener(
      Class<E> clazz, BungeeEventHandler<E> handler, byte priority) {
    PluginManager pluginManager = BungeeCord.getInstance().getPluginManager();
    EventBus eventBus =
        (EventBus)
            SimpleReflection.getField(pluginManager.getClass(), "eventBus").get(pluginManager);

    // Register the handler inside byPriorityMap
    final Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byPriorityMap =
        (Map<Class<?>, Map<Byte, Map<Object, Method[]>>>)
            SimpleReflection.getField(EventBus.class, "byListenerAndPriority").get(eventBus);
    final Map<Byte, Map<Object, Method[]>> byteMapMap =
        byPriorityMap.computeIfAbsent(clazz, $ -> new HashMap<>());
    Method declaredMethod = findDeclaredMethod(handler, clazz);
    Objects.requireNonNull(declaredMethod, "Method is null");

    Map<Object, Method[]> objectMap = byteMapMap.computeIfAbsent(priority, $ -> new HashMap<>());
    objectMap.put(handler, new Method[] {declaredMethod});

    // Bake listeners
    Method bakeHandlers = SimpleReflection.getMethod(EventBus.class, "bakeHandlers", Class.class);
    bakeHandlers.invoke(eventBus, clazz);

    return () -> objectMap.remove(handler);
  }

  private byte hookPriorityToEvent(HookProperties.Priority priority) {
    switch (priority) {
      case LOW:
        return EventPriority.LOW;
      case HIGH:
        return EventPriority.HIGH;
      case HIGHEST:
        return EventPriority.HIGHEST;
      case MONITOR:
        return EventPriority.LOWEST;
    }

    return -1;
  }
}
