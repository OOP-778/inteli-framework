package com.oop.inteliframework.config.property.property.custom;

import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.property.property.SerializedProperty;

/**
 * Serialize/Deserialize properties
 */
public interface PropertyHandler<T> {
    SerializedProperty toNode(T object);
    T fromNode(Node node);
}
