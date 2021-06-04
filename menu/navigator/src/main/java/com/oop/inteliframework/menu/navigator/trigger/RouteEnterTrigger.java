package com.oop.inteliframework.menu.navigator.trigger;

import com.oop.inteliframework.menu.menu.simple.InteliMenu;
import com.oop.inteliframework.menu.navigator.NavigatorComponent;
import com.oop.inteliframework.menu.navigator.route.NavigatorRoute;
import com.oop.inteliframework.menu.trigger.types.MenuTrigger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class RouteEnterTrigger implements MenuTrigger {

  private final Player player;
  private final InteliMenu menu;
  private final NavigatorComponent navigator;
  private final NavigatorRoute route;
  private final int slotTriggered;

  @Setter private boolean cancelled = false;
}
