package com.oop.inteliframework.config.node;

import lombok.NonNull;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@Accessors(fluent = true)
public class NodeValuable extends BaseNode {
    @NonNull
    private final Object value;

    public NodeValuable(String key, @Nullable ParentableNode parent, @NonNull Object value) {
        super(key, parent);
        this.value = value;
    }

    @Override
    public boolean isParentable() {
        return false;
    }

    @Override
    public Optional<ParentableNode> asParentable() {
        return Optional.empty();
    }

    @Override
    public Optional<NodeValuable> asValuable() {
        return Optional.of(this);
    }
}
