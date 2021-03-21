package com.oop.inteliframework.config.node.api;

import com.oop.inteliframework.commons.util.Preconditions;

import java.util.List;

/**
 * The base node interface
 */
public interface Node {
    List<String> comments();

    void appendComments(String... comments);

    default ParentNode asParent() {
        Preconditions.checkArgument(this instanceof ParentNode, "Failed to get as ParentNode because it's not");
        return ((ParentNode) this);
    }

    default ValueNode asValue() {
        Preconditions.checkArgument(this instanceof ValueNode, "Failed to get as ValueNode because it's not");
        return ((ValueNode) this);
    }

    default boolean isParent() {
        return this instanceof ParentNode;
    }
}
