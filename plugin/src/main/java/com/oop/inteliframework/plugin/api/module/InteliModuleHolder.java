package com.oop.inteliframework.plugin.api.module;

import com.oop.inteliframework.commons.util.InteliOptional;
import java.util.List;
import lombok.NonNull;

public interface InteliModuleHolder<T extends InteliModuleHolder> {

  @NonNull List<InteliModule> modules();

  T registerModule(final @NonNull InteliModule module);

  <R extends InteliModule> InteliOptional<R> moduleByClass(final @NonNull Class<R> clazz);

}
