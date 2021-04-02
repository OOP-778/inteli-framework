package com.oop.inteliframework;

import com.oop.inteliframework.animation.AnimatedText;
import com.oop.inteliframework.animation.InteliAnimationModule;
import com.oop.inteliframework.animation.parser.AnimationParser;
import com.oop.inteliframework.config.property.InteliPropertyModule;
import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.plugin.PlatformStarter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class VanillaStarter implements PlatformStarter<VanillaStarter> {
  private VanillaStarter() {
    startPlatform();
  }

  public static void main(String[] args) {
    new VanillaStarter();
    InteliPlatform.getInstance().registerModule(new InteliPropertyModule());
    InteliPlatform.getInstance().registerModule(new InteliAnimationModule());
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
