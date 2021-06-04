package com.oop.inteliframework.config.node;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.ParentNode;
import com.oop.inteliframework.config.node.api.ValueNode;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import com.oop.inteliframework.config.node.api.policy.NodeDuplicatePolicy;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.oop.inteliframework.commons.util.StringFormat.format;

@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class BaseParentNode extends BaseNode implements ParentNode {

  @Getter protected final Map<String, Node> nodes = new TreeMap<>(String::compareToIgnoreCase);

  protected static void _joinParents(
      @NonNull Map<String, Node> map,
      @NonNull String currentPath,
      @NonNull BaseParentNode node,
      @NonNull Predicate<Map.Entry<String, Node>> filter,
      boolean hierarchy) {
    for (Map.Entry<String, Node> nodeEntry :
        node.nodes.entrySet().stream()
            .sorted(Comparator.comparing(entry -> entry.getValue().isParent()))
            .collect(Collectors.toList())) {
      String path =
          currentPath + (currentPath.isEmpty() ? nodeEntry.getKey() : "." + nodeEntry.getKey());
      if (!filter.test(new AbstractMap.SimpleEntry<>(path, nodeEntry.getValue()))) continue;

      map.put(path, nodeEntry.getValue());
      if (nodeEntry.getValue() instanceof BaseParentNode && hierarchy)
        _joinParents(map, path, (BaseParentNode) nodeEntry.getValue(), filter, true);
    }
  }

  @Override
  public void merge(@NonNull ParentNode node, @NonNull NodeDuplicatePolicy policy) {
    for (Map.Entry<String, Node> mergingNodeEntry : node.map(NodeIterator.ALL).entrySet()) {
      switch (policy) {
        case THROW:
          if (nodes.containsKey(mergingNodeEntry.getKey()))
            throw new IllegalStateException(
                format("A node with key of {} already exists", mergingNodeEntry.getKey()));
          nodes.put(mergingNodeEntry.getKey(), mergingNodeEntry.getValue());
          break;

        case IGNORE:
          if (nodes.containsKey(mergingNodeEntry.getKey())) continue;
          nodes.put(mergingNodeEntry.getKey(), mergingNodeEntry.getValue());
          break;

        case REPLACE:
          nodes.put(mergingNodeEntry.getKey(), mergingNodeEntry.getValue());
          break;
      }
    }

    this.comments().clear();
    this.comments().addAll(node.comments());
  }

  @NotNull
  @Override
  public Iterator<Node> iterator() {
    return list(NodeIterator.ALL).iterator();
  }

  @Override
  public List<Node> list(
      @NonNull NodeIterator type,
      @Nullable Predicate<Node> filter,
      @Nullable Comparator<Node> comparator) {
    Function<Stream<Node>, Stream<Node>> filterStream =
        stream -> {
          if (filter == null) return stream;
          return stream.filter(filter);
        };

    Function<Stream<Node>, Stream<Node>> sortStream =
        stream -> {
          if (comparator == null) return stream;
          return stream.sorted(comparator);
        };

    final Stream<Node> stream;
    if (type == NodeIterator.ALL) stream = nodes.values().stream();
    else if (type == NodeIterator.HIERARCHY) {
      stream =
          nodes.values().stream()
              .flatMap(
                  node -> {
                    List<Node> nodes = new ArrayList<>();
                    if (node instanceof BaseParentNode) {
                      nodes.addAll(((BaseParentNode) node).nodes.values());
                    }

                    nodes.add(node);
                    return nodes.stream();
                  });
    } else {
      stream =
          nodes.values().stream().filter(node -> type != NodeIterator.PARENT || node.isParent());
    }

    return sortStream.apply(filterStream.apply(stream)).collect(Collectors.toList());
  }

  @Override
  public Map<String, Node> map(
      @NonNull NodeIterator iteratorType, @Nullable Predicate<Map.Entry<String, Node>> filter) {
    final Map<String, Node> finalMap = new LinkedHashMap<>();

    _joinParents(
        finalMap,
        "",
        this,
        pair -> {
          // Filter out by iterator type
          if (iteratorType == NodeIterator.PARENT && !(pair.getValue() instanceof ParentNode))
            return false;

          if (iteratorType == NodeIterator.VALUE && !(pair.getValue() instanceof ValueNode))
            return false;

          if (filter != null) return filter.test(pair);
          return true;
        },
        iteratorType == NodeIterator.HIERARCHY);

    return finalMap;
  }

  @Override
  public void assignNode(String path, Node node) {
    String[] splitPath = StringUtils.split(path, ".");
    Queue<String> pathQueue = new LinkedList<>(Arrays.asList(splitPath));

    BaseParentNode currentParent = this;

    while (!pathQueue.isEmpty()) {
      String key = pathQueue.poll();

      if (!pathQueue.isEmpty()) {
        BaseParentNode newParent = new BaseParentNode();

        currentParent.set(key, newParent);
        currentParent = newParent;
        continue;
      }

      // If was last entry
      currentParent.nodes.put(key, node);
    }
  }

  protected Optional<InteliPair<Node, BaseParentNode>> _get(String path) {
    String[] splitPath = StringUtils.split(path, ".");
    Queue<String> pathQueue = new LinkedList<>(Arrays.asList(splitPath));
    BaseParentNode currentParent = this;

    while (!pathQueue.isEmpty()) {
      String key = pathQueue.poll();

      if (!pathQueue.isEmpty()) {
        Node possibleParent = currentParent.nodes.get(key);
        if (!(possibleParent instanceof BaseParentNode)) return Optional.empty();

        currentParent = (BaseParentNode) possibleParent;
        continue;
      }

      // If was last entry
      return Optional.of(new InteliPair<>(currentParent.nodes.get(key), currentParent));
    }

    return Optional.empty();
  }

  @Override
  public Node get(String key, String notFoundMessage) {
    return _get(key)
        .map(InteliPair::getKey)
        .orElseThrow(() -> new IllegalStateException(notFoundMessage));
  }

  @Override
  public Node getOrAssign(String key, Supplier<Node> ifNotFound) {
    Optional<InteliPair<Node, BaseParentNode>> optNode = _get(key);
    return optNode
        .map(InteliPair::getKey)
        .orElseGet(
            () -> InteliOptional.of(ifNotFound.get()).use(node -> assignNode(key, node)).get());
  }

  @Override
  public Node getOrDefault(String path, Node node) {
    return _get(path).map(InteliPair::getKey).orElse(node);
  }

  @Override
  public void ifPresent(String path, Consumer<Node> nodeConsumer) {
    _get(path).map(InteliPair::getKey).ifPresent(nodeConsumer);
  }

  @Override
  public void forEach(Consumer<? super Node> consumer) {
    nodes.values().forEach(consumer);
  }

  @Override
  public Spliterator<Node> spliterator() {
    return nodes.values().spliterator();
  }

  public boolean isPresent(String path) {
    return isPresentAnd(path, $ -> true);
  }

  @Override
  public InteliOptional<Node> findAt(String path) {
    return InteliOptional.ofNullable(_get(path).map(InteliPair::getKey).orElse(null));
  }

  @Override
  public Optional<Node> remove(String path) {
    String[] split = StringUtils.split(path, ".");
    if (split.length <= 1) {
      return Optional.ofNullable(nodes.remove(split[0]));
    }

    Optional<InteliPair<Node, BaseParentNode>> optional = _get(path);
    if (!optional.isPresent()) {
      return Optional.empty();
    }

    InteliPair<Node, BaseParentNode> pair = optional.get();
    return Optional.of(pair.getValue().nodes.remove(split[split.length - 1]));
  }

  public boolean isPresentAnd(String path, Predicate<Node> and) {
    return _get(path).map(InteliPair::getKey).filter(and).isPresent();
  }

  public Node set(String path, Object object) {
    String[] splitPath = StringUtils.split(path, ".");
    Queue<String> pathQueue = new LinkedList<>(Arrays.asList(splitPath));

    Node node = new BaseValueNode(object);
    BaseParentNode currentParent = this;

    while (!pathQueue.isEmpty()) {
      String key = pathQueue.poll();

      if (!pathQueue.isEmpty()) {
        BaseParentNode newParent = new BaseParentNode();

        currentParent.set(key, newParent);
        currentParent = newParent;
        continue;
      }

      // If was last entry
      currentParent.assignNode(key, node);
      return node;
    }

    return node;
  }

  @Override
  public void dump() {
    for (Map.Entry<String, Node> stringNodeEntry : map(NodeIterator.HIERARCHY).entrySet()) {
      for (String comment : stringNodeEntry.getValue().comments()) {
        System.out.println("# " + comment);
      }
      System.out.println("=== " + stringNodeEntry.getKey() + " ===");
    }
  }
}
