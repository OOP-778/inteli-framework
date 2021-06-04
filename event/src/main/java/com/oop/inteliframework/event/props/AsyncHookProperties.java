package com.oop.inteliframework.event.props;

import com.oop.inteliframework.event.HookedEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

@Accessors(chain = true, fluent = true)
@Getter
public class AsyncHookProperties<T> extends HookProperties<T> {

  // This runs on the thread where event was called
  @Setter @Nullable private BiConsumer<T, HookedEvent<T>> preCall;
}
