package com.oop.inteliframework;

import com.oop.inteliframework.config.InteliConfigModule;
import com.oop.inteliframework.configs.ConfigsTester;
import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.plugin.PlatformStarter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class VanillaStarter implements PlatformStarter<VanillaStarter> {
  private VanillaStarter() {
    startPlatform();
  }

  public static void main(String[] args) {
    new VanillaStarter();
    InteliPlatform.getInstance().registerModule(new InteliConfigModule());

    ConfigsTester.doTest();
  }

  @Override
  public Path dataDirectory() {
    return Paths.get(System.getenv("os.dir"));
  }

  @Override
  public String name() {
    return "vanilla";
  }
}
