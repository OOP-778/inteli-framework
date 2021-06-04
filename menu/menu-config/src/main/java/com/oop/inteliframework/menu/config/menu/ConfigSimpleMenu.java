package com.oop.inteliframework.menu.config.menu;

import com.oop.inteliframework.menu.attribute.AttributeComponent;
import com.oop.inteliframework.menu.attribute.Attributes;
import com.oop.inteliframework.menu.button.IButton;
import com.oop.inteliframework.menu.config.MenuConfiguration;
import com.oop.inteliframework.menu.designer.MenuDesigner;
import com.oop.inteliframework.menu.menu.simple.InteliMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ConfigSimpleMenu extends InteliMenu implements ConfigMenu<InteliMenu> {
  private final MenuConfiguration config;

  public ConfigSimpleMenu(Player player, MenuConfiguration configuration) {
    super(player);
    this.config = configuration;

    // Load buttons
    parseLayout();

    // Set title
    setTitleSupplier($ -> configuration.getTitle());

    // Make sure that menu is rebuilt on open
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
        if (buttonMap.containsKey(c)) continue;

        config
            .findButton(cb -> cb.getLetter().toCharArray()[0] == c)
            .ifPresent(
                configButton -> {
                  buttonMap.put(configButton.getLetter().toCharArray()[0], configButton.build());
                });
      }

      designer.row(rowCount, row, buttonMap);
      rowCount++;
    }
  }
}
