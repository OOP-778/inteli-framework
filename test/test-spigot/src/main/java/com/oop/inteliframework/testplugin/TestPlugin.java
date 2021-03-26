package com.oop.inteliframework.testplugin;

import com.oop.inteliframework.animation.InteliAnimationModule;
import com.oop.inteliframework.command.bukkit.BukkitCommandExecutor;
import com.oop.inteliframework.command.bukkit.BukkitCommandRegistry;
import com.oop.inteliframework.command.element.argument.NoValueArgument;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.config.api.configuration.PlainConfig;
import com.oop.inteliframework.hologram.Hologram;
import com.oop.inteliframework.hologram.HologramController;
import com.oop.inteliframework.hologram.animated.AnimatedLine;
import com.oop.inteliframework.hologram.builder.HologramBuilder;
import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.menu.InteliMenus;
import com.oop.inteliframework.menu.config.MenuConfiguration;
import com.oop.inteliframework.menu.config.MenuLoader;
import com.oop.inteliframework.menu.interfaces.MenuItemBuilder;
import com.oop.inteliframework.menu.interfaces.MenuUtil;
import com.oop.inteliframework.plugin.PlatformStarter;
import com.oop.inteliframework.task.InteliTaskFactory;
import com.oop.inteliframework.task.type.inteli.InteliTaskController;
import com.oop.inteliframework.testplugin.menu.InteliMenuItemBuilder;
import com.oop.inteliframework.testplugin.menu.TestMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestPlugin extends JavaPlugin implements Listener, PlatformStarter<TestPlugin> {

  private HologramController hologramController;
  private Hologram hologram;

  @Override
  public void onEnable() {
    startPlatform();
    registerModule(new InteliTaskFactory().registerController(new InteliTaskController(1)));
    registerModule(new InteliAnimationModule());

    hologramController = HologramController
            .builder()
            .plugin(this)
            .executorService(Executors.newScheduledThreadPool(2))
            .build();

    BukkitCommandRegistry commandRegistry = new BukkitCommandRegistry(this);
    commandRegistry.register(
        new Command()
            .labeled("helloPeople")
            .addAlias("helloP")
            .onExecute(
                ((executor, commandData) -> {
                  executor.as(BukkitCommandExecutor.class).commandSender.sendMessage("Hello!");
                }))
            .addChild(new NoValueArgument().labeled("--silent")));

    Bukkit.getPluginManager().registerEvents(this, this);
  }

  //  @EventHandler
  //  public void onChat(AsyncPlayerChatEvent event) {
  //    BukkitAudiences bukkitAudiences = BukkitAudiences.create(this);
  //    TextComponent text = Component.text("Hello! <This is where it goes>");
  //
  //    NBTItem nbtItem =
  //        new NBTItem(
  //            SimpleItemFactory.itemOf(InteliMaterial.DIAMOND)
  //                .applyMeta(
  //                    meta -> {
  //                      meta.enchant(InteliEnchantment.DAMAGE_ALL, 15);
  //                      meta.name("YAY");
  //                    })
  //                .asBukkitStack());
  //
  //    Component component =
  //        text.replaceText(
  //            builder -> {
  //              builder.matchLiteral("<This is where it goes>");
  //              builder.replacement(
  //                  Component.text("YAY")
  //                      .hoverEvent(
  //                          HoverEvent.showItem(
  //                              HoverEvent.ShowItem.of(
  //                                  Key.key("minecraft:diamond"),
  //                                  1,
  //                                  BinaryTagHolder.of(nbtItem.getCompound().toString())))));
  //            });
  //    bukkitAudiences.all().sendMessage(component);
  //  }

//  @EventHandler public void onChat(AsyncPlayerChatEvent event) {
//    File file = new File(getDataFolder(), "testMenu.yml");
//
//    InteliMenus.register(this, InteliMenuItemBuilder::new);
//
//    InteliMenus.getInteliMenus().registerMenuUtil(new MenuUtil() {
//      @Override
//      public void ensureSync(Runnable runnable) {
//        runnable.run();
//      }
//
//      @Override
//      public void async(Runnable runnable) {
//        runnable.run();
//      }
//
//      @Override
//      public void playSound(String name, float volume, float pitch, float yaw) {
//
//      }
//    });
//
//    MenuLoader menuLoader = new MenuLoader();
//    menuLoader.setItemProvider(name -> InteliMaterial.matchMaterial(name).parseItem());
//    PlainConfig nodes = new PlainConfig(file);
//    nodes.load();
//
//    new TestMenu(event.getPlayer(), new MenuConfiguration(nodes, menuLoader)).openAction(null);
//  }

  @EventHandler public void onChat(AsyncPlayerChatEvent event) {
    if (hologram == null) {
      hologram = new HologramBuilder()
              // Set global default refresh rate
              .refreshRate(1)
              .addView(view -> {
                view.lines()
                        .add(new AnimatedLine(player -> player.isOp() ? "<erase[fade, reverse]=OOP IS GOD/>" : "<erase[reverse]=EZ/> <erase[fade, reverse]=Stuff/>"));
              })
              .build();

      hologram.setLocation(event.getPlayer().getLocation());
      hologramController.registerHologram(hologram);
    }

    hologram.setLocation(event.getPlayer().getLocation());
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
