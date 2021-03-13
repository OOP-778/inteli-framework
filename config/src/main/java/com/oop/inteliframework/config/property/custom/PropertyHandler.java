package com.oop.inteliframework.config.property.custom;

import com.oop.inteliframework.config.node.Node;

/**
 * Serialize/Deserialize properties
 */
public interface PropertyHandler<T> {
    Node toNode(String underName, T object);
    T fromNode(Node node);
}
