package com.oop.inteliframework.event.bukkit;

import com.oop.inteliframework.event.EventSystem;
import com.oop.inteliframework.event.HookedEvent;
import com.oop.inteliframework.event.props.HookProperties;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Set;

public class BukkitEventSystem extends EventSystem<Event> {
  public BukkitEventSystem() {
    super(Event.class);

    platform()
        .starter()
        .hookDisable(
            () -> {
              for (Map<Class<? super Event>, Set<HookedEvent<? super Event>>> value :
                  hooked.values()) {
                for (Set<HookedEvent<? super Event>> hookedEvents : value.values()) {
                  for (HookedEvent<? super Event> hookedEvent : hookedEvents) {
                    ((HandlerList) ((BukkitHookedEvent) hookedEvent).getEvent()).unregisterAll();
                  }
                }
              }
            });
  }

  @Override
  public <E extends Event> HookedEvent<E> hook(Class<E> eventClass, HookProperties<E> properties) {
    Set<HookedEvent<? super Event>> hookedEvents =
        getHookedEvents(properties.priority(), eventClass);

    BukkitHookedEvent hookedEvent = new BukkitHookedEvent(properties);
    hookedEvent.setUnhook(
        () -> {
          HandlerList.unregisterAll(hookedEvent.getEvent());
          hookedEvents.remove(hookedEvent);
        });

    hookedEvent.event =
        ($, event) -> {
          if (eventClass.isInstance(event)) {
            hookedEvent.handle(event);
          }
        };
    Bukkit.getPluginManager()
        .registerEvent(
            eventClass,
            hookedEvent.event,
            hookPriorityToEvent(properties.priority()),
            hookedEvent.event,
            (Plugin) platform().starter());

    hookedEvents.add(hookedEvent);
    return hookedEvent;
  }

  private EventPriority hookPriorityToEvent(HookProperties.Priority priority) {
    switch (priority) {
      case LOW:
        return EventPriority.LOW;
      case HIGH:
        return EventPriority.HIGH;
      case HIGHEST:
        return EventPriority.HIGHEST;
      case MONITOR:
        return EventPriority.MONITOR;
    }

    return null;
  }

  @Override
  public void call(Event event) {
    Bukkit.getPluginManager().callEvent(event);
  }
}
