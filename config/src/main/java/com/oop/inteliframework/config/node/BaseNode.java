package com.oop.inteliframework.config.node;

import static com.oop.inteliframework.commons.util.CollectionHelper.addAndReturn;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base node implementation
 */
@Accessors(fluent = true)
@EqualsAndHashCode
public abstract class BaseNode implements Node {

    @Getter
    private final List<String> comments = new LinkedList<>();

    @NotNull
    @Getter
    @Setter
    private String key;

    @Nullable
    private ParentNode parent;

    public BaseNode(String key, ParentNode parent) {
        this.key = key;
        this.parent = parent;
    }

    @Override
    public Optional<ParentNode> parent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public @NonNull String path() {
        if (parent == null) {
            return key;
        }
        return addAndReturn(parents(), key)
            .stream()
            .filter(it -> it.trim().length() != 0)
            .collect(Collectors.joining("."));
    }

    private List<String> parents() {
        List<String> parents = new LinkedList<>();
        ParentNode current = parent;

        while (true) {
            if (current != null) {
                parents.add(current.key());
            }

            if (current == null) {
                break;
            }

            current = current.parent().orElse(null);
        }

        Collections.reverse(parents);
        return parents;
    }

    @Override
    public void appendComments(String... comments) {
        this.comments.addAll(Arrays.asList(comments));
    }

    public void parent(@NonNull ParentNode parent) {
        this.parent = parent;
    }
}
