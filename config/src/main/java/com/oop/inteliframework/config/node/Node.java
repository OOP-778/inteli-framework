package com.oop.inteliframework.config.node;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

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
    Optional<ParentableNode> parent();

    @NonNull
    String path();

    // Key of the node
    String key();

    // Returns parentable
    // If it's not parentable, returns a null
    Optional<ParentableNode> asParentable();

    // Returns node as valuable
    //If it's not valuable, returns null
    Optional<NodeValuable> asValuable();

    List<String> comments();

    void comment(String... comments);

    void parent(@NonNull ParentableNode parentableNode);
}
