package com.oop.inteliframework.config.property;

import com.oop.inteliframework.config.node.Node;

public interface Property<T> {
    static <T extends Number> Property<T> create(T defaultValue) {
        return null;
    }

    Node toNode();
}
