package com.oop.intelimenus;

import com.oop.intelimenus.config.MenuLoader;
import com.oop.intelimenus.interfaces.Menu;
import com.oop.intelimenus.interfaces.MenuItemBuilder;
import com.oop.intelimenus.interfaces.MenuUtil;
import com.oop.intelimenus.listener.MenuListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InteliMenus {

    private static InteliMenus instance;

    @Getter
    private final JavaPlugin owningPlugin;

    @Getter
    private @NonNull
    final Function<ItemStack, MenuItemBuilder> menuItemBuilderFunction;

    @Getter
    private MenuUtil util;

    @Getter
    private @NonNull MenuLoader loader;

    @Getter
    private final ScheduledExecutorService scheduler;

    private InteliMenus(
        JavaPlugin owningPlugin,
        Function<ItemStack, MenuItemBuilder> menuItemBuilderFunction
    ) {
        instance = this;
        this.owningPlugin = owningPlugin;
        this.menuItemBuilderFunction = menuItemBuilderFunction;

        Bukkit.getPluginManager().registerEvents(new MenuListener(), owningPlugin);
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread inteliMenusThread = new Thread(r, "InteliMenusThread");
            inteliMenusThread.setUncaughtExceptionHandler((t, e) -> {
                e.printStackTrace();
            });
            return inteliMenusThread;
        });
    }

    public static InteliMenus register(JavaPlugin owningPlugin,
        Function<ItemStack, MenuItemBuilder> menuItemBuilder) {
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
            if (onlinePlayer.getOpenInventory().getTopInventory() != null && Menu.class
                .isAssignableFrom(onlinePlayer.getOpenInventory().getTopInventory().getClass())) {
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
