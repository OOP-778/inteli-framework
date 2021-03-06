package com.oop.inteliframework.testplugin;

import com.oop.inteliframework.hologram.Hologram;
import com.oop.inteliframework.hologram.HologramController;
import com.oop.inteliframework.hologram.builder.HologramBuilder;
import com.oop.inteliframework.recipe.RecipesController;
import com.oop.inteliframework.recipe.shaped.ShapedRecipeBuilder;
import com.oop.inteliframework.recipe.shapeless.ShapelessRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;

public class TestPlugin extends JavaPlugin implements Listener {
    private HologramController hologramController;
    private Hologram hologram;
    private RecipesController controller;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        //Bukkit.getPluginManager().registerEvents(new PacketCatcher(), this);
        hologramController = HologramController
                .builder()
                .plugin(this)
                .executorService(Executors.newScheduledThreadPool(2))
                .build();

        controller = RecipesController.getInstance();

        Bukkit.getPluginManager().registerEvents(controller, this);

        controller.register(
                ShapelessRecipe.builder()
                        .required(2, new ItemStack(Material.DIRT))
                        .required(2, new ItemStack(Material.APPLE))
                        .required(new ItemStack(Material.IRON_AXE))
                        .required(5, new ItemStack(Material.IRON_INGOT))
                        .required(2, new ItemStack(Material.STONE))
                        .result(new ItemStack(Material.ANVIL))
                        .build()
        );

        ItemStack superStack = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta meta = superStack.getItemMeta();
        meta.setDisplayName("§4§lSuper chestplate");
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
        meta.addEnchant(Enchantment.THORNS, 5, true);
        superStack.setItemMeta(meta);

        controller.register(
                ShapedRecipeBuilder.builder()
                        .pattern(
                                "% X %",
                                "% % %",
                                "% % %"
                        )
                        .item('%', new ItemStack(Material.DIAMOND_BLOCK, 5))
                        .result(superStack)
                        .build()
        );

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (hologram == null) {
            hologram = new HologramBuilder()
                    // Set global default refresh rate
                    .refreshRate(1)

                    // OP's view
                    .addView(view -> {
                        view.addRule((holo, player) -> player.isOp());
                        view.addLines(lines -> lines
                                .displayItem(player -> player.getInventory().getItem(0))
                                .displayText(player -> "&cYour op!")
                                .displayText(HumanEntity::getName)
                                .displayText("&cOOOOOOOF"));
                    })
                    .addView(view -> {
                        view.addLines(lines -> lines
                                .displayItem(player -> player.getInventory().getItem(0))
                                .displayText(player -> "&cYour bot.")
                                .displayText(HumanEntity::getName)
                                .displayText("&cOOOOOOOF"));
                    })
                    .build();

            hologram.setLocation(event.getPlayer().getLocation());
            hologramController.registerHologram(hologram);
        }

        hologram.setLocation(event.getPlayer().getLocation());
    }

    @Override
    public void onDisable() {
        hologramController.onDisable();
    }
}
