package com.oop.inteliframework.plugin.api;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.plugin.api.module.InteliModule;
import com.oop.inteliframework.plugin.api.module.InteliModuleHolder;
import java.util.List;
import lombok.NonNull;

public interface InteliPlugin<T extends InteliPlugin> extends InteliModuleHolder<T> {

  @Override
  default @NonNull List<InteliModule> modules() {
    return InteliPlatform.getInstance().modules();
  }

  @Override
  default T registerModule(@NonNull InteliModule module) {
    InteliPlatform.getInstance().registerModule(module);
    return (T) this;
  }

  @Override
  default <R extends InteliModule> InteliOptional<R> moduleByClass(
      @NonNull Class<R> clazz) {
    return InteliPlatform.getInstance().moduleByClass(clazz);
  }

}
