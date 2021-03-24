package com.oop.inteliframework.config.property.property;

import com.oop.inteliframework.config.node.api.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
public class SerializedProperty {

    @Nullable
    private String suggestedKey;
    private Node node;

    public static SerializedProperty of(Node node) {
        return new SerializedProperty(null, node);
    }
}
