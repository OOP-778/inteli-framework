package com.oop.inteliframework.config.property;

import com.oop.inteliframework.config.node.api.Node;

/** Handles config section stuff, must have at least one property field */
public interface Configurable {
  default void onPreload(Node node) {}

  default void onLoad(Node node) {}
}
