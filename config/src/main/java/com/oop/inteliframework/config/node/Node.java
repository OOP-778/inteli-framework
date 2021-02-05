package com.oop.inteliframework.config.node;

import java.util.List;
import java.util.Optional;
import lombok.NonNull;

/**
 * The base node interface
 */
public interface Node {

    /**
     * Checks if the node is parentable
     *
     * @return boolean
     */
    boolean isParentable();

    /**
     * Returns optional parent
     *
     * @return Optional<ParentableNode>
     */
    Optional<ParentNode> parent();

    @NonNull
    String path();

    // Key of the node
    String key();

    // Returns parentable
    // If it's not parentable, returns a null
    Optional<ParentNode> asParent();

    // Returns node as valuable
    //If it's not valuable, returns null
    Optional<ValueNode> asValue();

    List<String> comments();

    void appendComments(String... comments);

    void parent(@NonNull ParentNode parentNode);

    default ParentNode asParentSafe() {
        return (ParentNode) this;
    }

    default ValueNode asValueSafe() {
        return (ValueNode) this;
    }
}
