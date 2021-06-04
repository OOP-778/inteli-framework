package com.oop.inteliframework.message.api;

import com.oop.inteliframework.message.InteliMessageModule;
import com.oop.inteliframework.plugin.module.InteliModule;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;

import java.util.function.Function;

public interface Sendable extends InteliModule {
  void send(Audience receiver);

  default void send(Function<AudienceProvider, Audience> audienceFunction) {
    send(
        audienceFunction.apply(
            platform().safeModuleByClass(InteliMessageModule.class).audiences()));
  }
}
