package com.oop.inteliframework.menu.trigger.types;

import com.oop.inteliframework.menu.button.IButton;
import com.oop.inteliframework.menu.menu.simple.InteliMenu;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

public class ButtonClickTrigger implements MenuTrigger {

  @Getter private final ClickType click;

  @Getter private final InventoryAction action;

  @Getter private final int slot;

  @Getter private final InteliMenu menu;
  @Getter private final IButton button;
  @Getter private final Player player;
  @Getter @Setter private boolean cancelled;

  public ButtonClickTrigger(
      InteliMenu menu,
      IButton button,
      int slot,
      ClickType click,
      InventoryAction action,
      Player player) {
    this.menu = menu;
    this.slot = slot;
    this.click = click;
    this.action = action;
    this.button = button;
    this.player = player;
  }

  public boolean isRightClick() {
    return this.click.isRightClick();
  }

  public boolean isLeftClick() {
    return this.click.isLeftClick();
  }

  public boolean isShiftClick() {
    return this.click.isShiftClick();
  }
}
