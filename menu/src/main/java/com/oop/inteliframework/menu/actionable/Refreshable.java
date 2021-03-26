package com.oop.inteliframework.menu.actionable;

import com.oop.inteliframework.menu.interfaces.Comparable;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.Predicate;

public interface Refreshable<T> extends Comparable<T> {

  static <T extends Refreshable> void refreshAll(Class clazz, Predicate<T> filter) {
    Bukkit.getOnlinePlayers()
        .forEach(
            player -> {
              Inventory inventory = player.getOpenInventory().getTopInventory();
              if (inventory == null) {
                return;
              }

              InventoryHolder holder = inventory.getHolder();
              if (!(holder instanceof Refreshable)) {
                return;
              }

              if (!holder.getClass().isAssignableFrom(clazz)) {
                return;
              }
              if (filter != null && !filter.test((T) holder)) {
                return;
              }

              if (!((Refreshable) holder).compare(holder)) {
                return;
              }

              ((Refreshable) holder).refreshAction();
            });
  }

  static <T extends Refreshable> void refreshAll(T object, Predicate<T> filter) {
    refreshAll(object.getClass(), filter);
  }

  static <T extends Refreshable> void refreshAll(T object) {
    refreshAll(object, null);
  }

  void refreshAction();
}
