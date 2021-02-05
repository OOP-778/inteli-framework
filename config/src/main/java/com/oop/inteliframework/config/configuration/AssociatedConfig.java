package com.oop.inteliframework.config.configuration;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class AssociatedConfig<T> {

    // The plain config that represents this
    @NonNull
    private PlainConfig plainConfig;


    public void reload() {

    }
}
