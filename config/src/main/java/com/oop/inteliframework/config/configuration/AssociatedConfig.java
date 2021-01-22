package com.oop.inteliframework.config.configuration;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AssociatedConfig<T> {
    // The plain config that represents this
    @NonNull
    private final PlainConfig plainConfig;

    // The holder that is associated with this config
    @NonNull
    private final T holder;
}
