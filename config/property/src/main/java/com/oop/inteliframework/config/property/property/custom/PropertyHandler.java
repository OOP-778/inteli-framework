package com.oop.inteliframework.config.property.property.custom;

import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.property.InteliPropertyModule;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.plugin.InteliPlatform;

import javax.annotation.Nullable;

/** Serialize/Deserialize properties */
public interface PropertyHandler<T> {
  SerializedProperty toNode(T object);

  @Deprecated()
  T fromNode(Node node);

  default T fromNode(Node node, @Nullable Node parent) {
    return fromNode(node);
  }

  Class<T> getObjectClass();

  default void register() {
    InteliPlatform
            .getInstance()
            .safeModuleByClass(InteliPropertyModule.class)
            .registerHandler(this);
  }
}
