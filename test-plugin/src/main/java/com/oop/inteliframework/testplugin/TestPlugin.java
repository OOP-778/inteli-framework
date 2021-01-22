package com.oop.inteliframework.testplugin;

import com.oop.inteliframework.hologram.Hologram;
import com.oop.inteliframework.hologram.HologramController;
import com.oop.inteliframework.hologram.builder.HologramBuilder;
import com.oop.inteliframework.scoreboard.IScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;

public class TestPlugin extends JavaPlugin implements Listener {
    private HologramController hologramController;
    private Hologram hologram;

    private IScoreboard scoreboard;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        //Bukkit.getPluginManager().registerEvents(new PacketCatcher(), this);
        hologramController = HologramController
                .builder()
                .plugin(this)
                .executorService(Executors.newScheduledThreadPool(2))
                .build();

//        scoreboard = new IScoreboard();
//        scoreboard.setTitleSupplier(player -> "&cBig Testawgaawg");
//        scoreboard.getLines().add(player -> "&cHey!awgawgwgawg");
//        scoreboard.getLines().add(player -> "&c2!wgagwafaw");
//        scoreboard.getLines().add(player -> "&c25!awgagwagwagaw");
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
