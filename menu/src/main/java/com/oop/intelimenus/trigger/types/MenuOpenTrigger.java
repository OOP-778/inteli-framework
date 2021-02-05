package com.oop.intelimenus.trigger.types;

import com.oop.intelimenus.menu.simple.InteliMenu;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class MenuOpenTrigger implements MenuTrigger {

    @Getter
    private final InteliMenu menu;

    @Getter
    @Setter
    private boolean cancelled;

    @Getter
    private final Player player;

    public MenuOpenTrigger(InteliMenu menu, Player player) {
        this.menu = menu;
        this.player = player;
    }
}
