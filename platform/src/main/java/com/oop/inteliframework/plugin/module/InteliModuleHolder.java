package com.oop.inteliframework.plugin.module;

import com.oop.inteliframework.commons.util.InteliOptional;
import lombok.NonNull;

import java.util.List;

import static com.oop.inteliframework.commons.util.StringFormat.format;

public interface InteliModuleHolder<T extends InteliModuleHolder> {

  @NonNull
  List<InteliModule> modules();

  <T extends InteliModule> void removeModule(Class<T> moduleClazz);

  default <T extends InteliModule> void removeModule(T module) {
    removeModule(module.getClass());
  }

  T registerModule(final @NonNull InteliModule... modules);

  <R extends InteliModule> InteliOptional<R> moduleByClass(final @NonNull Class<R> clazz);

  default <R extends InteliModule> R safeModuleByClass(final @NonNull Class<R> clazz) {
    return moduleByClass(clazz)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    format(
                        "Module by class {} doesn't exist and was trying to get it safe :/",
                        clazz.getSimpleName())));
  }
}
