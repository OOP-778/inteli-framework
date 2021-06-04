package com.oop.inteliframework.menu.navigator;

import com.oop.inteliframework.menu.component.Component;
import com.oop.inteliframework.menu.component.ComponentHolder;
import com.oop.inteliframework.menu.navigator.route.NavigatorRoute;
import com.oop.inteliframework.menu.navigator.route.Routes;
import com.oop.inteliframework.menu.trigger.TriggerComponent;
import com.oop.inteliframework.menu.trigger.types.MenuCloseTrigger;
import com.oop.inteliframework.menu.trigger.types.MenuOpenTrigger;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Accessors(chain = true, fluent = true)
public class NavigatorComponent implements Component<NavigatorComponent> {

  @NonNull @Getter private final NavigatorHistory history;
  @NonNull @Getter private final Routes routes = new Routes(this);

  @Setter @Getter private Player viewer;

  @Override
  public NavigatorComponent clone() {
    return this;
  }

  @Override
  public void onAdd(ComponentHolder<?> holder) {
    holder.applyComponent(
        TriggerComponent.class,
        triggerComp -> {
          triggerComp.addTrigger(MenuCloseTrigger.class, event -> {});

          triggerComp.addTrigger(
              MenuOpenTrigger.class,
              event -> {
                // Handle menu open
              });
        });
  }

  public void addPath(String path, Consumer<NavigatorRoute> pathBuilder) {}
}
