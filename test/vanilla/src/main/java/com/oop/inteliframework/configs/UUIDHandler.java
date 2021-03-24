package com.oop.inteliframework.configs;

import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.BaseValueNode;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.config.property.property.custom.PropertyHandler;

import java.util.UUID;

public class UUIDHandler implements PropertyHandler<UUID> {
    @Override
    public SerializedProperty toNode(UUID object) {
        return new SerializedProperty(null, new BaseValueNode(object.toString()));
    }

    @Override
    public UUID fromNode(Node node) {
        return UUID.randomUUID();
    }
}
