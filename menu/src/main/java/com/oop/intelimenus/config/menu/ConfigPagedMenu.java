package com.oop.intelimenus.config.menu;

import com.oop.intelimenus.config.MenuConfiguration;
import com.oop.intelimenus.menu.paged.InteliPagedMenu;
import com.oop.intelimenus.menu.simple.InteliMenu;
import org.bukkit.entity.Player;

public class ConfigPagedMenu<T> extends InteliPagedMenu<T> implements ConfigMenu<InteliMenu> {
    public ConfigPagedMenu(Player player, MenuConfiguration configuration) {
        super(player);
    }
}
