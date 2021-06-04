package com.oop.inteliframework.plugin;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.plugin.logger.InteliLogger;
import com.oop.inteliframework.plugin.module.InteliModule;
import com.oop.inteliframework.plugin.module.InteliModuleHolder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InteliPlatform implements InteliModuleHolder<InteliPlatform> {

  @Getter private static InteliPlatform instance;
  private final IdentityHashMap<Class<? extends InteliModule>, InteliModule> modules =
      new IdentityHashMap<>();
  private final List<Runnable> disableHooks = new LinkedList<>();

  @Getter
  @Accessors(fluent = true)
  private final PlatformStarter starter;

  private InteliLogger logger;

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
    for (Runnable disableHook : disableHooks) {
      disableHook.run();
    }

    instance = null;
  }

  public void hookDisable(Runnable runnable) {
    this.disableHooks.add(runnable);
  }

  public <T extends PlatformStarter> T starterAs(Class<T> clazz) {
    return (T) starter;
  }

  @Override
  public @NonNull List<InteliModule> modules() {
    return new ArrayList<>(modules.values());
  }

  @Override
  public <T extends InteliModule> void removeModule(Class<T> moduleClazz) {
    modules.remove(moduleClazz);
  }

  @Override
  public InteliPlatform registerModule(@NonNull InteliModule... modules) {
    for (InteliModule module : modules) {
      this.modules.put(module.getClass(), module);
    }
    return this;
  }

  @Override
  public <R extends InteliModule> InteliOptional<R> moduleByClass(@NonNull Class<R> clazz) {
    return InteliOptional.ofNullable((R) modules.get(clazz));
  }

  public Stream<InteliModule> allModules(Predicate<InteliModule> predicate) {
    return modules().stream().filter(predicate);
  }

  public <T extends InteliModule> Collection<T> allModulesThatExtend(Class<T> clazz) {
    return allModules(module -> clazz.isAssignableFrom(module.getClass()))
        .map(module -> (T) module)
        .collect(Collectors.toList());
  }

  public InteliLogger logger() {
    return logger;
  }
}
