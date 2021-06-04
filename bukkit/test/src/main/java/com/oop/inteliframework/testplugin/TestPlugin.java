package com.oop.inteliframework.testplugin;

import com.oop.inteliframework.bukkit.entity.tracker.InteliPlayerTracker;
import com.oop.inteliframework.command.bukkit.BukkitArguments;
import com.oop.inteliframework.command.bukkit.BukkitCommandRegistry;
import com.oop.inteliframework.command.element.argument.Arguments;
import com.oop.inteliframework.command.element.argument.NoValueArgument;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.dependency.common.CommonLibraryManager;
import com.oop.inteliframework.dependency.common.CommonLogAdapter;
import com.oop.inteliframework.entity.hologram.Hologram;
import com.oop.inteliframework.entity.hologram.InteliHologramFactory;
import com.oop.inteliframework.event.Events;
import com.oop.inteliframework.event.InteliEventModule;
import com.oop.inteliframework.event.bukkit.BukkitEventSystem;
import com.oop.inteliframework.plugin.PlatformStarter;
import com.oop.inteliframework.task.InteliTaskFactory;
import com.oop.inteliframework.task.bukkit.BukkitTaskController;
import com.oop.inteliframework.task.type.inteli.InteliTaskController;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class TestPlugin extends JavaPlugin implements Listener, PlatformStarter<TestPlugin> {
  private Hologram hologram;

  public TestPlugin() {}

  @Override
  public void onEnable() {
    if (!getDataFolder().exists()) getDataFolder().mkdirs();

    new CommonLibraryManager(
            new CommonLogAdapter(), (URLClassLoader) getClassLoader(), getDataFolder())
        .load();

    startPlatform();
    registerModule(new InteliTaskFactory());

    InteliTaskController taskController = new InteliTaskController(2);
    safeModuleByClass(InteliTaskFactory.class)
        .registerController(taskController)
        .registerController(new BukkitTaskController());

    registerModule(new InteliEventModule());
    safeModuleByClass(InteliEventModule.class).registerSystem(new BukkitEventSystem());

    registerModule(new InteliPlayerTracker(taskController));
    registerModule(new InteliHologramFactory(taskController));
    registerModule(new BukkitCommandRegistry());

    safeModuleByClass(BukkitCommandRegistry.class)
        .register(
            new Command()
                .labeled("testcommand")
                .addChild(
                    BukkitArguments.playerArgument()
                        .addChild(
                            new Command()
                                .labeled("give")
                                .onExecute(
                                    ((executor, commandData) -> {
                                      System.out.println(
                                          commandData.getAsOptional("player").orElse("null"));
                                    }))
                                .addChild(
                                    Arguments.numberArg()
                                        .addChild(new NoValueArgument().labeled("--silent"))))));

    Events.Simple.hook(
        AsyncPlayerChatEvent.class,
        event -> {
          if (hologram == null) {
            hologram =
                Hologram.builder()
                    .location(event.getPlayer().getLocation())
                    .refreshRate(1, TimeUnit.SECONDS)
                    .addView(
                        view -> {
                          view.addLines(
                              lines -> {
                                lines.displayItem($ -> new ItemStack(Material.DIAMOND_ORE));
                                lines.displayText("&cwafawaawf");
                              });
                        })
                    .build();
            safeModuleByClass(InteliHologramFactory.class).registerHologram(hologram);
            return;
          }

          hologram.setLocation(event.getPlayer().getLocation());
        });
  }

  @Override
  public Path dataDirectory() {
    return getDataFolder().toPath();
  }

  @Override
  public String name() {
    return getDescription().getName();
  }

  @Override
  public void onDisable() {
    platform().onDisable();
  }
}
