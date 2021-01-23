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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class TestPlugin extends JavaPlugin implements Listener {
    private HologramController hologramController;
    private Hologram hologram;

    private IScoreboard scoreboard;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        // Enable it if you need catch some packets! :)
        //Bukkit.getPluginManager().registerEvents(new PacketCatcher(), this);
        hologramController = HologramController
                .builder()
                .plugin(this)
                .executorService(Executors.newScheduledThreadPool(2))
                .build();

        List<String> lines = new ArrayList<>(
                Arrays.asList(
                        "#1e7037----------------------qqqq",
                        "#70571e----------------------qqq",
                        "#2a2563----------------------",
                        "#360d35eqqqqqqqqqqqqqqqqqqqqqqqewe",
                        "#a6a14cqwrqwerwqetrwetrwetr",
                        "#20b6d4tregjiwoejrgowegrgwergregqewerqwergtwertwetwte",
                        "#52703ftretowerjowegwnegor",
                        "#1a3b40wreoijwopgjoguroiweurgoiwehrgoiuhwerogihwoerghiweougrhweogrw"
                        )
        );

        scoreboard = new IScoreboard();
        scoreboard.setTitleSupplier(player -> lines.get(ThreadLocalRandom.current().nextInt(lines.size() - 1)));
        scoreboard.getLines().add(player -> "&cHey!awgaqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqwgwgawg");
        scoreboard.getLines().add(player -> "&c2!wgagwafqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqaw");
        scoreboard.getLines().add(player -> "&c25!awqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqgagwagwagaw");
        scoreboard.getLines().add(player -> lines.get(ThreadLocalRandom.current().nextInt(lines.size() - 1)));
        scoreboard.getLines().add(player -> lines.get(ThreadLocalRandom.current().nextInt(lines.size() - 1)));
        scoreboard.getLines().add(player -> lines.get(ThreadLocalRandom.current().nextInt(lines.size() - 1)));
        scoreboard.getLines().add(player -> lines.get(ThreadLocalRandom.current().nextInt(lines.size() - 1)));
        scoreboard.getLines().add(player -> lines.get(ThreadLocalRandom.current().nextInt(lines.size() - 1)));
        scoreboard.getLines().add(player -> lines.get(ThreadLocalRandom.current().nextInt(lines.size() - 1)));
        scoreboard.getLines().add(player -> lines.get(ThreadLocalRandom.current().nextInt(lines.size() - 1)));
        scoreboard.getLines().add(player -> lines.get(ThreadLocalRandom.current().nextInt(lines.size() - 1)));
        scoreboard.getLines().add(player -> lines.get(ThreadLocalRandom.current().nextInt(lines.size() - 1)));

        new BukkitRunnable() {
            @Override
            public void run() {
                scoreboard.updateAll();
            }
        }.runTaskTimerAsynchronously(this, 1, 1);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        scoreboard.add(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        scoreboard.remove(event.getPlayer());
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
