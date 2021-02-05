package com.oop.inteliframework.testplugin;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.intelimenus.attribute.Attributes;
import com.oop.intelimenus.button.IButton;
import com.oop.intelimenus.button.state.StateComponent;
import com.oop.intelimenus.button.state.StateRequestComponent;
import com.oop.intelimenus.config.ConfigDataKeys;
import com.oop.intelimenus.config.MenuConfiguration;
import com.oop.intelimenus.config.menu.ConfigMenu;
import com.oop.intelimenus.config.menu.ConfigSimpleMenu;
import com.oop.intelimenus.data.DataComponent;
import com.oop.intelimenus.trigger.TriggerComponent;
import com.oop.intelimenus.trigger.types.MenuCloseTrigger;
import org.bukkit.entity.Player;

public class TestMenu extends ConfigSimpleMenu {

    public TestMenu(Player player,
        MenuConfiguration configuration) {
        super(player, configuration);

        onStateRequest("head", button -> {
            boolean op = getViewer().get().isOp();
            return button
                .getComponent(StateComponent.class)
                .flatMap(st -> InteliOptional.fromOptional(st.getState(op ? "op" : "non-op")))
                .orElse(null);
        });
    }
}
