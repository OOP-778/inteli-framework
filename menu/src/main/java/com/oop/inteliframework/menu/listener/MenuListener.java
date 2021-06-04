package com.oop.inteliframework.menu.listener;

import com.oop.inteliframework.event.Events;
import com.oop.inteliframework.menu.menu.simple.InteliMenu;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class MenuListener implements Listener {
  public MenuListener() {
    Events.Simple.hook(
        InventoryOpenEvent.class,
        event -> {
          if (!(event.getInventory().getHolder() instanceof InteliMenu)) {
            return;
          }

          InteliMenu menu = (InteliMenu) event.getInventory().getHolder();
          menu.onOpen(event);
        });

    Events.Simple.hook(
        InventoryClickEvent.class,
        event -> {
          if (event.getWhoClicked().getOpenInventory().getTopInventory() == null) {
            return;
          }
          if (!(event.getWhoClicked().getOpenInventory().getTopInventory().getHolder()
              instanceof InteliMenu)) {
            return;
          }
          if (event.getSlot() < 0) {
            return;
          }

          InteliMenu menu =
              (InteliMenu) event.getWhoClicked().getOpenInventory().getTopInventory().getHolder();
          if (event.getClickedInventory().getHolder() == menu) {
            menu.onClick(event);
          } else {
            menu.onBottomClick(event);
          }
        });
  }
}
