package com.oop.inteliframework.plugin;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.plugin.module.InteliModule;
import com.oop.inteliframework.plugin.module.InteliModuleHolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.oop.inteliframework.plugin.logger.InteliLogger;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

public class InteliPlatform implements InteliModuleHolder<InteliPlatform> {

  @Getter
  private static InteliPlatform instance;
  private final List<InteliModule> modules = new ArrayList<>();

  private InteliLogger logger;

  @Getter
  @Accessors(fluent = true)
  private final PlatformStarter starter;

  public InteliPlatform(PlatformStarter starter) {
    this.starter = starter;
    this.logger = new InteliLogger(starter.name());
  }

  protected static void initPlatform(PlatformStarter starter) {
    if (instance != null) {
      throw new UnsupportedOperationException("Platform already initialized!");
    }

    instance = new InteliPlatform(starter);
  }

  public void onDisable() {
    instance = null;
  }

  public <T extends PlatformStarter> T starterAs(Class<T> clazz) {
    return (T) starter;
  }

  @Override
  public @NonNull List<InteliModule> modules() {
    return Collections.unmodifiableList(modules);
  }

  @Override
  public InteliPlatform registerModule(@NonNull InteliModule ...modules) {
    Collections.addAll(this.modules, modules);
    return this;
  }

  @Override
  public <R extends InteliModule> InteliOptional<R> moduleByClass(
      @NonNull Class<R> clazz) {
    return InteliOptional.fromOptional(
        modules
            .stream()
            .filter(module -> clazz.isAssignableFrom(module.getClass()))
            .findFirst()
            .map(module -> (R) module)
    );
  }

  public InteliLogger logger() {
    return logger;
  }
}
