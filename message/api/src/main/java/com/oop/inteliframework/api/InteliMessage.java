package com.oop.inteliframework.api;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.function.Consumer;

/**
 * Base message
 */
public interface InteliMessage<T extends InteliMessage> {

  T replace(Consumer<TextReplacementConfig.Builder> builderConsumer);
  Component toComponent();
  void send(Audience audience);
}
