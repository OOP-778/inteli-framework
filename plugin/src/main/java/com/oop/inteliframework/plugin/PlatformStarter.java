package com.oop.inteliframework.plugin;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.plugin.module.InteliModule;
import com.oop.inteliframework.plugin.module.InteliModuleHolder;
import lombok.NonNull;

import java.nio.file.Path;
import java.util.List;

public interface PlatformStarter<T extends PlatformStarter> extends InteliModuleHolder<T> {

  @Override
  default @NonNull List<InteliModule> modules() {
    return InteliPlatform.getInstance().modules();
  }

  @Override
  default T registerModule(@NonNull InteliModule ...modules) {
    InteliPlatform.getInstance().registerModule(modules);
    return (T) this;
  }

  @Override
  default <R extends InteliModule> InteliOptional<R> moduleByClass(@NonNull Class<R> clazz) {
    return InteliPlatform.getInstance().moduleByClass(clazz);
  }

  default void startPlatform() {
    InteliPlatform.initPlatform(this);
  }

  Path dataDirectory();

  String name();

}
