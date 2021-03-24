package com.oop.inteliframework.testplugin;

import com.oop.inteliframework.command.bukkit.BukkitCommandExecutor;
import com.oop.inteliframework.command.bukkit.BukkitCommandRegistry;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.item.SimpleItemFactory;
import com.oop.inteliframework.item.comp.InteliEnchantment;
import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.plugin.PlatformStarter;
import com.oop.inteliframework.task.InteliTaskFactory;
import com.oop.inteliframework.task.type.inteli.InteliTaskController;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public class TestPlugin extends JavaPlugin implements Listener, PlatformStarter<TestPlugin> {
  @Override
  public void onEnable() {
    startPlatform();
    registerModule(new InteliTaskFactory().registerController(new InteliTaskController(1)));

    BukkitCommandRegistry commandRegistry = new BukkitCommandRegistry(this);
    commandRegistry.register(
            new Command()
              .labeled("helloPeople")
              .addAlias("helloP")
              .onExecute(((executor, commandData) -> {
                executor.as(BukkitCommandExecutor.class).commandSender.sendMessage("Hello!");
              }))
    );

    Bukkit.getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void onChat(AsyncPlayerChatEvent event) {
    BukkitAudiences bukkitAudiences = BukkitAudiences.create(this);
    TextComponent text = Component.text("Hello! <This is where it goes>");

    NBTItem nbtItem =
        new NBTItem(
            SimpleItemFactory.itemOf(InteliMaterial.DIAMOND)
                .applyMeta(
                    meta -> {
                      meta.enchant(InteliEnchantment.DAMAGE_ALL, 15);
                      meta.name("YAY");
                    })
                .asBukkitStack());

    Component component =
        text.replaceText(
            builder -> {
              builder.matchLiteral("<This is where it goes>");
              builder.replacement(
                  Component.text("YAY")
                      .hoverEvent(
                          HoverEvent.showItem(
                              HoverEvent.ShowItem.of(
                                  Key.key("minecraft:diamond"),
                                  1,
                                  BinaryTagHolder.of(nbtItem.getCompound().toString())))));
            });
    bukkitAudiences.all().sendMessage(component);
  }

  @Override
  public Path dataDirectory() {
    return getDataFolder().toPath();
  }

  @Override
  public String name() {
    return getDescription().getName();
  }
}
