package com.oop.inteliframework;

import com.oop.inteliframework.config.property.InteliPropertyModule;
import com.oop.inteliframework.configs.ConfigsTester;
import com.oop.inteliframework.dependency.common.CommonLibraryManager;
import com.oop.inteliframework.dependency.common.CommonLogAdapter;
import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.plugin.PlatformStarter;

import java.io.File;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VanillaStarter implements PlatformStarter<VanillaStarter> {
  private VanillaStarter() {
    startPlatform();
  }

  public static void main(String[] args) {
    new VanillaStarter();
    InteliPlatform.getInstance().registerModule(new InteliPropertyModule());

    new CommonLibraryManager(
            new CommonLogAdapter(), (URLClassLoader) VanillaStarter.class.getClassLoader(), new File(System.getProperty("user.dir")))
            .load();

    ConfigsTester.doTest();
  }

  @Override
  public Path dataDirectory() {
    return Paths.get(System.getProperty("user.dir"));
  }

  @Override
  public String name() {
    return "vanilla";
  }
}
