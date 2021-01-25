package com.oop.inteliframework.config.node;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.oop.inteliframework.commons.util.CollectionHelper.addAndReturn;

/**
 * The base node implementation
 */
@Accessors(fluent = true)
public abstract class BaseNode implements Node {
    @Getter
    private final List<String> comments = new LinkedList<>();

    @NotNull
    @Getter
    @Setter
    private String key;

    @Nullable
    private ParentableNode parent;

    public BaseNode(String key, ParentableNode parent) {
        this.key = key;
        this.parent = parent;
    }

    @Override
    public Optional<ParentableNode> parent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public @NonNull String path() {
        if (parent == null) return key;
        return addAndReturn(parents(), key)
                .stream()
                .filter(it -> it.trim().length() != 0)
                .collect(Collectors.joining("."));
    }

    private List<String> parents() {
        List<String> parents = new LinkedList<>();
        ParentableNode current = parent;

        while (true) {
            if (current != null)
                parents.add(current.key());

            if (current == null)
                break;

            current = current.parent().orElse(null);
        }

        Collections.reverse(parents);
        return parents;
    }

    @Override
    public void comment(String... comments) {
        this.comments.addAll(Arrays.asList(comments));
    }

    public void parent(@NonNull ParentableNode parent) {
        this.parent = parent;
    }
}
