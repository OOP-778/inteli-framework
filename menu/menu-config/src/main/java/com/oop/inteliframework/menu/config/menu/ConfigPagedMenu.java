package com.oop.inteliframework.menu.config.menu;

import com.oop.inteliframework.menu.config.MenuConfiguration;
import com.oop.inteliframework.menu.menu.paged.InteliPagedMenu;
import com.oop.inteliframework.menu.menu.simple.InteliMenu;
import org.bukkit.entity.Player;

public class ConfigPagedMenu<T> extends InteliPagedMenu<T> implements ConfigMenu<InteliMenu> {
  public ConfigPagedMenu(Player player, MenuConfiguration configuration) {
    super(player);
  }
}
