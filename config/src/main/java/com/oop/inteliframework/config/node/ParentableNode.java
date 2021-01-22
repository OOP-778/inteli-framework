package com.oop.inteliframework.config.node;

import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Accessors(fluent = true)
public class ParentableNode extends BaseNode {
    private final Map<String, Node> children = new TreeMap<>(String::compareToIgnoreCase);

    public ParentableNode(@NotNull String key, @Nullable ParentableNode parent) {
        super(key, parent);
    }

    @Override
    public boolean isParentable() {
        return true;
    }

    @Override
    public Optional<ParentableNode> asParentable() {
        return Optional.of(this);
    }

    @Override
    public Optional<NodeValuable> asValuable() {
        return Optional.empty();
    }

    public Optional<Node> findNodeAt(String path) {
        String[] paths = StringUtils.split(path, ".");
        Node node = children.get(paths[0]);

        if (node instanceof ParentableNode && paths.length != 1)
            return Optional.ofNullable(getNodeAt(Arrays.copyOfRange(paths, 1, paths.length)));

        return Optional.ofNullable(node);
    }

    protected Node getNodeAt(String[] paths) {
        Node node = children.get(paths[0]);
        if (node != null && paths.length != 1)
            getNodeAt(Arrays.copyOfRange(paths, 1, paths.length));

        return node;
    }

    public void merge(ParentableNode node) {
        for (Node value : node.children.values()) {
            value.parent(this);
            children.put(value.key(), value);
        }
    }
}
