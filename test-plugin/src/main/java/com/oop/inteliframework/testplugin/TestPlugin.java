package com.oop.inteliframework.testplugin;

import com.oop.inteliframework.plugin.PlatformStarter;
import com.oop.inteliframework.task.InteliTaskFactory;
import com.oop.inteliframework.task.type.inteli.InteliTaskController;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public class TestPlugin extends JavaPlugin implements Listener, PlatformStarter {

  @Override
  public void onEnable() {
    startPlatform();
    registerModule(new InteliTaskFactory().registerController(new InteliTaskController(1)));
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
