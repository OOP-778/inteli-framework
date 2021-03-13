package com.oop.inteliframework.config.node;

import com.google.gson.internal.LinkedTreeMap;
import com.oop.inteliframework.commons.util.InteliOptional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.oop.inteliframework.commons.util.StringFormat.format;

@Accessors(fluent = true)
@EqualsAndHashCode
public class ParentNode extends BaseNode implements Iterable<Node> {

  @Getter private final Map<String, Node> nodes = new LinkedTreeMap<>(String::compareToIgnoreCase);

  public ParentNode(@NotNull String key, @Nullable ParentNode parent) {
    super(key, parent);
  }

  public ParentNode(@NotNull String key) {
    this(key, null);
  }

  @Override
  public boolean isParentable() {
    return true;
  }

  @Override
  public Optional<ParentNode> asParent() {
    return Optional.of(this);
  }

  @Override
  public Optional<ValueNode> asValue() {
    return Optional.empty();
  }

  public ValueNode getAsValueOrThrow(String path, String errorMessage) {
    InteliOptional<Node> at = findAt(path);
    return at.filter(node -> !node.isParentable())
        .map(Node::asValueSafe)
        .orElseThrow(() -> new IllegalStateException(errorMessage));
  }

  public ValueNode getAsValueOrThrow(String path) {
    InteliOptional<Node> at = findAt(path);
    return at.filter(node -> !node.isParentable())
        .map(Node::asValueSafe)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    format("Failed to find value at path: {} in {}", path(), path)));
  }

  public InteliOptional<Node> findAt(String path) {
    String[] paths = StringUtils.split(path, ".");
    Node node = nodes.get(paths[0]);

    if (node instanceof ParentNode && paths.length != 1) {
      return InteliOptional.ofNullable(
          ((ParentNode) node).getAt(Arrays.copyOfRange(paths, 1, paths.length), false));
    }

    return InteliOptional.ofNullable(node);
  }

  protected Node getAt(String[] paths, boolean createIfNotPresent) {
    Node node = nodes.get(paths[0]);
    if (node != null && paths.length != 1 && node instanceof ParentNode) {
      return ((ParentNode) node)
          .getAt(Arrays.copyOfRange(paths, 1, paths.length), createIfNotPresent);
    }

    return node;
  }

  public void merge(ParentNode node) {
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
    return list(NodeIteratorType.ALL).iterator();
  }

  @NonNull
  public List<Node> list(NodeIteratorType type) {
    if (type == NodeIteratorType.ALL) return new LinkedList<>(nodes.values());

    return nodes.values().stream()
        .filter(node -> type != NodeIteratorType.PARENTABLE || node.isParentable())
        .collect(Collectors.toCollection(LinkedList::new));
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
    for (String comment : comments()) {
      System.out.println("#" + comment);
    }
    System.out.println("== " + path() + " ==");

    List<Node> nodes = new LinkedList<>(nodes().values());
    nodes.sort(Comparator.comparing(node -> !node.isParentable()));

    for (Node node : this) {
      if (node.isParentable()) {
        node.asParent().get().dump();

      } else {
        ValueNode valueNode = node.asValue().get();
        for (String comment : valueNode.comments()) {
          System.out.println("#" + comment);
        }
        System.out.println(format("Key={}, Value={}", node.path(), valueNode.value()));
      }
    }
  }

  public boolean isPresent(String path) {
    return isPresentAnd(path, $ -> true);
  }

  public boolean isPresentAnd(String path, Predicate<Node> and) {
    return findAt(path).filter(and).isPresent();
  }

  public void updateChildrenParents() {
    for (Node value : nodes.values()) {
      value.parent(this);
      if (value.isParentable()) {
        value.asParentSafe().updateChildrenParents();
      }
    }
  }
}
