package com.oop.inteliframework.config.node;

import com.google.gson.internal.LinkedTreeMap;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static com.oop.inteliframework.commons.util.StringFormat.format;

@Accessors(fluent = true)
public class ParentableNode extends BaseNode implements Iterable<Node> {

    @Getter
    private final Map<String, Node> nodes = new LinkedTreeMap<>(String::compareToIgnoreCase);

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
        Node node = nodes.get(paths[0]);

        if (node instanceof ParentableNode && paths.length != 1)
            return Optional.ofNullable(((ParentableNode) node).getNodeAt(Arrays.copyOfRange(paths, 1, paths.length)));

        return Optional.ofNullable(node);
    }

    protected Node getNodeAt(String[] paths) {
        Node node = nodes.get(paths[0]);
        if (node != null && paths.length != 1 && node instanceof ParentableNode)
            return ((ParentableNode) node).getNodeAt(Arrays.copyOfRange(paths, 1, paths.length));

        return node;
    }

    public void merge(ParentableNode node) {
        for (Node value : node.nodes.values()) {
            value.parent(this);
            nodes.put(value.key(), value);
        }

        this.comments().clear();
        this.comments().addAll(node.comments());
    }

    @NotNull
    @Override
    public Iterator<Node> iterator() {
        return nodes.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super Node> consumer) {
        nodes.values().forEach(consumer);
    }

    @Override
    public Spliterator<Node> spliterator() {
        return nodes.values().spliterator();
    }

    public void dump() {
        for (String comment : comments())
            System.out.println("#" + comment);
        System.out.println("== " + path() + " ==");

        List<Node> nodes = new LinkedList<>(nodes().values());
        nodes.sort(Comparator.comparing(node -> !node.isParentable()));

        for (Node node : this) {
            if (node.isParentable())
                node.asParentable().get().dump();

            else {
                NodeValuable nodeValuable = node.asValuable().get();
                for (String comment : nodeValuable.comments())
                    System.out.println("#" + comment);
                System.out.println(format("Key={}, Value={}", node.path(), nodeValuable.value()));
            }
        }
    }
}
