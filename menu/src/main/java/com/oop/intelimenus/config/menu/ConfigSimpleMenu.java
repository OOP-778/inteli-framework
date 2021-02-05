package com.oop.intelimenus.config.menu;

import com.oop.intelimenus.attribute.AttributeComponent;
import com.oop.intelimenus.attribute.Attributes;
import com.oop.intelimenus.button.IButton;
import com.oop.intelimenus.config.ConfigButton;
import com.oop.intelimenus.config.MenuConfiguration;
import com.oop.intelimenus.designer.MenuDesigner;
import com.oop.intelimenus.menu.simple.InteliMenu;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public abstract class ConfigSimpleMenu extends InteliMenu implements ConfigMenu<InteliMenu> {
    private final MenuConfiguration config;

    public ConfigSimpleMenu(Player player, MenuConfiguration configuration) {
        super(player);
        this.config = configuration;

        // Load buttons
        parseLayout();

        // Set title
        setTitleSupplier($ -> configuration.getTitle());

        applyComponent(AttributeComponent.class, ac -> ac.addAttribute(Attributes.REBUILD_ON_OPEN));
    }

    // Parse layout to buttons
    public void parseLayout() {
        List<String> layout = config.getLayout();
        setSize(layout.size() * 9);
        MenuDesigner designer = new MenuDesigner(this, null);

        Map<Character, IButton> buttonMap = new HashMap<>();
        int rowCount = 1;

        for (String row : layout) {
            for (char c : row.toCharArray()) {
                if (buttonMap.containsKey(c))
                    continue;

                config
                    .findButton(cb -> cb.getLetter().toCharArray()[0] == c)
                    .ifPresent(configButton -> {
                        buttonMap.put(configButton.getLetter().toCharArray()[0], configButton.build());
                        System.out.println("Found button " + configButton.getLetter());
                    });
            }

            designer.row(rowCount, row, buttonMap);
            rowCount++;
        }
    }
}
