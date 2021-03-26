package com.oop.inteliframework.testplugin.menu;

import com.oop.inteliframework.menu.button.state.StateComponent;
import com.oop.inteliframework.menu.config.MenuConfiguration;
import com.oop.inteliframework.menu.config.menu.ConfigSimpleMenu;
import org.bukkit.entity.Player;

public class TestMenu extends ConfigSimpleMenu {
    public TestMenu(Player player, MenuConfiguration configuration) {
        super(player, configuration);

        onStateRequest("head", button -> button.getComponent(StateComponent.class).map(states -> states.getState(
                player.isFlying() ? "flying" : "non-flying"
        ).get()).get());
    }
}
