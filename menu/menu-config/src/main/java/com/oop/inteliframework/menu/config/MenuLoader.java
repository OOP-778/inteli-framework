package com.oop.inteliframework.menu.config;

import com.oop.inteliframework.config.api.configuration.PlainConfig;
import com.oop.inteliframework.menu.config.modifiers.ModifierHolder;
import com.oop.inteliframework.menu.config.modifiers.def.ActionModifier;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Getter
public class MenuLoader extends ModifierHolder {
  private final Map<String, MenuConfiguration> loadedMenus = new HashMap<>();

  @Setter private Function<String, ItemStack> itemProvider;

  public MenuLoader() {
    registerModifier(new ActionModifier());
  }

  public void load(File... menusDirectories) {
    try {
      for (File menusDirectory : menusDirectories) {
        if (menusDirectory == null || !menusDirectory.exists()) {
          continue;
        }

        File[] menuFiles = menusDirectory.listFiles();
        for (File menuFile : Objects.requireNonNull(menuFiles)) {
          try {

            PlainConfig menuConfig = new PlainConfig(menuFile);
            loadedMenus.put(
                menuFile.getName().split("\\.")[0].toLowerCase(),
                new MenuConfiguration(menuConfig, this));

          } catch (Throwable menuError) {
            throw new IllegalStateException(
                "Failed to load menu by name "
                    + menuFile.getName().split("\\.")[0]
                    + " at path: "
                    + menuFile.getPath());
          }
        }
      }
    } catch (Throwable loadThrw) {
      throwError("Failed to load menus cause", loadThrw);
    }
  }

  protected void throwError(String message, Throwable inerhit) throws IllegalStateException {
    throw new IllegalStateException(message, inerhit);
  }
}
