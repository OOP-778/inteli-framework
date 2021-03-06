package com.oop.inteliframework.config.node;

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
    private final Map<String, Node> nodes = new TreeMap<>(String::compareToIgnoreCase);

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
            return Optional.ofNullable(getNodeAt(Arrays.copyOfRange(paths, 1, paths.length)));

        return Optional.ofNullable(node);
    }

    protected Node getNodeAt(String[] paths) {
        Node node = nodes.get(paths[0]);
        if (node != null && paths.length != 1)
            getNodeAt(Arrays.copyOfRange(paths, 1, paths.length));

        return node;
    }

    public void merge(ParentableNode node) {
        for (Node value : node.nodes.values()) {
            value.parent(this);
            nodes.put(value.key(), value);
        }
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
        System.out.println("DUMPING == " + key() + " ==");
        System.out.println("");

        List<Node> nodes = new LinkedList<>(nodes().values());
        nodes.sort(Comparator.comparing(node -> !node.isParentable()));

        for (Node node : this) {
            if (node.isParentable())
                node.asParentable().get().dump();
            else
                System.out.println(format("Key={}, Value={}", node.key(), node.asValuable().get().value()));
        }
    }
}
