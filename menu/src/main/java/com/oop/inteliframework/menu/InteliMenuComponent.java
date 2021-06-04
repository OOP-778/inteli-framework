package com.oop.inteliframework.menu;

import com.oop.inteliframework.menu.interfaces.Menu;
import com.oop.inteliframework.menu.listener.MenuListener;
import com.oop.inteliframework.plugin.module.InteliModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class InteliMenuComponent implements InteliModule {
  public InteliMenuComponent() {
    platform()
        .hookDisable(
            () -> {
              for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getOpenInventory().getTopInventory() != null
                    && Menu.class.isAssignableFrom(
                        onlinePlayer.getOpenInventory().getTopInventory().getClass())) {
                  onlinePlayer.closeInventory();
                }
              }
            });

    new MenuListener();
  }
}
