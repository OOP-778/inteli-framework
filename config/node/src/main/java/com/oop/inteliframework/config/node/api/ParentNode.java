package com.oop.inteliframework.config.node.api;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import com.oop.inteliframework.config.node.api.policy.NodeDuplicatePolicy;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** Parent node setup stores key/value */
public interface ParentNode extends Node, Iterable<Node> {

  /** Merge in values from provided ParentNode */
  void merge(@NonNull ParentNode node, @NonNull NodeDuplicatePolicy policy);

  /** Merge in values from provided ParentNode */
  default void merge(@NonNull ParentNode node) {
    merge(node, NodeDuplicatePolicy.REPLACE);
  }

  /**
   * Set & serialize an object at path
   *
   * @param path the path can contain dots and they will be split at them
   * @param object the object you're trying to set
   * @throws IllegalStateException if object fails to serialize
   */
  <T> Node set(String path, T object);

  /**
   * List nodes by IteratorType
   *
   * @param iteratorType that you want to list with
   * @param filter optional filter to filter out nodes you don't want
   * @param comparator optional comparator to sort nodes
   */
  List<Node> list(
      @NonNull NodeIterator iteratorType,
      @Nullable Predicate<Node> filter,
      @Nullable Comparator<Node> comparator);

  /**
   * List nodes by IteratorType
   *
   * @param iteratorType that you want to list with
   * @param filter optional filter to filter out nodes you don't want
   */
  default List<Node> list(@NonNull NodeIterator iteratorType, @Nullable Predicate<Node> filter) {
    return list(iteratorType, filter, null);
  }

  /**
   * List nodes by IteratorType
   *
   * @param iteratorType that you want to list with
   */
  default List<Node> list(@NonNull NodeIterator iteratorType) {
    return list(iteratorType, null);
  }

  /**
   * Map nodes by iteratorType
   *
   * @param iteratorType that you want to map with
   * @param filter optional filter to filter out nodes you don't want
   */
  Map<String, Node> map(
      @NonNull NodeIterator iteratorType, @Nullable Predicate<Map.Entry<String, Node>> filter, boolean mark);

  /**
   * Map nodes by iteratorType
   *
   * @param iteratorType that you want to map with
   */
  default Map<String, Node> map(@NonNull NodeIterator iteratorType) {
    return map(iteratorType, null, false);
  }

  /**
   * Map nodes by iteratorType
   *
   * @param iteratorType that you want to map with
   * @param mark when value keys starts with dots, it breaks hierarchy looper, so you can mark it and work with it when serializing
   */
  default Map<String, Node> map(@NonNull NodeIterator iteratorType, boolean mark) {
    return map(iteratorType, null, mark);
  }

  /**
   * Assign node to this ParentNode
   *
   * @param key key that you're assigning at
   * @param node that you're assign
   * @param split Should it split at dots and create sub sections for it
   */
  void assignNode(String key, Node node, boolean split);

  /**
   * Get a node at a key
   *
   * @param key key can contain dots
   * @param notFoundMessage if the node is not found message
   */
  Node get(String key, String notFoundMessage);

  /**
   * Get a node at a key
   *
   * @param key key can contain dots
   */
  default Node get(String key) {
    return get(key, "Failed to find a node at " + key);
  }

  /**
   * Get or assign if not present
   *
   * @param key the key
   * @param supplier supplier
   */
  Node getOrAssign(String key, @NonNull Supplier<Node> supplier);

  /**
   * @param path the default node
   * @return the node at the key or the default node
   */
  Node getOrDefault(String path, Node node);

  /**
   * Consume node at path if present
   *
   * @param path path
   * @param nodeConsumer consumer
   */
  void ifPresent(String path, Consumer<Node> nodeConsumer);

  /** Print out whole structure */
  void dump();

  /** Check if node exists */
  boolean isPresent(String path);

  /** Find an node at specific path */
  InteliOptional<Node> findAt(String path);

  /** Remove a node at specific path */
  Optional<Node> remove(String path);


}
