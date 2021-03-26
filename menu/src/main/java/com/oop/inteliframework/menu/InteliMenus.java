package com.oop.inteliframework.menu;

import com.oop.inteliframework.menu.interfaces.Menu;
import com.oop.inteliframework.menu.interfaces.MenuItemBuilder;
import com.oop.inteliframework.menu.interfaces.MenuUtil;
import com.oop.inteliframework.menu.listener.MenuListener;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class InteliMenus {

  private static InteliMenus instance;

  @Getter private final JavaPlugin owningPlugin;

  @Getter private @NonNull final Function<ItemStack, MenuItemBuilder> menuItemBuilderFunction;
  @Getter private final ScheduledExecutorService scheduler;
  @Getter private MenuUtil util;

  private InteliMenus(
      JavaPlugin owningPlugin, Function<ItemStack, MenuItemBuilder> menuItemBuilderFunction) {
    instance = this;
    this.owningPlugin = owningPlugin;
    this.menuItemBuilderFunction = menuItemBuilderFunction;

    Bukkit.getPluginManager().registerEvents(new MenuListener(), owningPlugin);
    scheduler =
        Executors.newSingleThreadScheduledExecutor(
            r -> {
              Thread inteliMenusThread = new Thread(r, "InteliMenusThread");
              inteliMenusThread.setUncaughtExceptionHandler(
                  (t, e) -> {
                    e.printStackTrace();
                  });
              return inteliMenusThread;
            });
  }

  public static InteliMenus register(
      JavaPlugin owningPlugin, Function<ItemStack, MenuItemBuilder> menuItemBuilder) {
    return new InteliMenus(owningPlugin, menuItemBuilder);
  }

  public static InteliMenus getInteliMenus() {
    return instance;
  }

  public void registerMenuUtil(MenuUtil util) {
    this.util = util;
  }

  public void disable() {
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      if (onlinePlayer.getOpenInventory().getTopInventory() != null
          && Menu.class.isAssignableFrom(
              onlinePlayer.getOpenInventory().getTopInventory().getClass())) {
        onlinePlayer.closeInventory();
      }
    }

    scheduler.shutdown();
    try {
      scheduler.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      scheduler.shutdownNow();
    }
  }
}
