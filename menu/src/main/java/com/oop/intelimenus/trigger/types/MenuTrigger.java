package com.oop.intelimenus.trigger.types;

import com.oop.intelimenus.interfaces.Menu;
import org.bukkit.entity.Player;

public interface MenuTrigger {

    Menu getMenu();

    boolean isCancelled();

    void setCancelled(boolean cancelled);

    Player getPlayer();

    default <T extends Menu> T getMenu(Class<T> type) {
        return (T) getMenu();
    }
}
