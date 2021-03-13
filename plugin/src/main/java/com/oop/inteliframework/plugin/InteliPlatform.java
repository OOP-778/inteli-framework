package com.oop.inteliframework.plugin;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.plugin.api.module.InteliModule;
import com.oop.inteliframework.plugin.api.module.InteliModuleHolder;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;

public abstract class InteliPlatform implements InteliModuleHolder<InteliPlatform> {

  @Getter
  private static InteliPlatform instance;
  private final List<InteliModule> modules = new ArrayList<>();

  public static void initPlatform(@NonNull InteliPlatform platform) {
    if (instance != null) {
      throw new UnsupportedOperationException("Platform already initialized!");
    }

    instance = platform;
  }

  @Override
  public @NonNull List<InteliModule> modules() {
    return modules;
  }

  @Override
  public InteliPlatform registerModule(@NonNull InteliModule module) {
    modules.add(module);
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
}
