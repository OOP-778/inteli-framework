package com.oop.inteliframework.menu.trigger.types;

import com.oop.inteliframework.menu.menu.simple.InteliMenu;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class MenuCloseTrigger implements MenuTrigger {

  @Getter private final InteliMenu menu;
  @Getter private final Player player;
  @Getter @Setter private boolean cancelled;

  public MenuCloseTrigger(InteliMenu menu, Player player) {
    this.menu = menu;
    this.player = player;
  }
}
