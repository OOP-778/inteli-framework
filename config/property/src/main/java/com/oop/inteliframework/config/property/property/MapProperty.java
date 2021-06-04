package com.oop.inteliframework.config.property.property;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.BaseValueNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.ParentNode;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.oop.inteliframework.commons.util.Helper.use;
import static com.oop.inteliframework.commons.util.StringFormat.format;
import static com.oop.inteliframework.config.property.loader.Loader.loaderFrom;
import static com.oop.inteliframework.config.property.serializer.Serializer.serializerFor;
import static com.oop.inteliframework.config.property.util.Helper.cloneMap;
import static com.oop.inteliframework.config.property.util.Helper.isPrimitive;

@AllArgsConstructor
@ToString
public class MapProperty<K, V, M extends Map> implements Property<M> {

  @NonNull private final Class<K> keyClass;

  @NonNull private final Class<V> valueClass;

  @NonNull protected M map;

  public static <K, V, M extends Map<K, V>> MapProperty<K, V, M> from(
      M map, Class<K> keyClass, Class<V> valueClass, InteliPair<K, V>... entries) {
    return new MapProperty<>(
        keyClass,
        valueClass,
        use(
            map,
            () -> {
              for (InteliPair<K, V> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
              }
            }));
  }

  @Override
  public void fromNode(Node node) {
    Preconditions.checkArgument(
        node instanceof ParentNode, "MapProperty can only load from ParentNode!");

    Function<Node, K> keyLoader;
    Function<Node, V> valueLoader;
    keyLoader = loaderFrom(keyClass);
    valueLoader = loaderFrom(valueClass);

    for (Map.Entry<String, Node> nodeEntry : node.asParent().map(NodeIterator.ALL).entrySet()) {

      K key = keyLoader.apply(new BaseValueNode(nodeEntry.getKey()));
      V value = valueLoader.apply(nodeEntry.getValue());

      map.put(key, value);
    }
  }

  @Override
  public SerializedProperty toNode() {
    BaseParentNode node = new BaseParentNode();

    Function<K, SerializedProperty> keySerializer;
    Function<V, SerializedProperty> valueSerializer;
    keySerializer = serializerFor(keyClass);
    valueSerializer = serializerFor(valueClass);

    for (Map.Entry entry : (Set<Map.Entry<K, V>>) map.entrySet()) {
      SerializedProperty serializedKey = keySerializer.apply((K) entry.getKey());
      SerializedProperty serializedValue = valueSerializer.apply((V) entry.getValue());

      Preconditions.checkArgument(
          isPrimitive(serializedKey.getNode().asValue().value()),
          format(
              "Failed to serialize map property because key is not a primitive value but a section. Key: {}",
              entry.getKey()));

      String entryKey =
          serializedValue.getSuggestedKey() != null
              ? serializedValue.getSuggestedKey()
              : serializedKey.getNode().asValue().value().toString();
      node.assignNode(entryKey, serializedValue.getNode());
    }

    return SerializedProperty.of(node);
  }

  @Override
  public M get() {
    return (M) cloneMap((Map<K, V>) map);
  }

  @Override
  public Class<M> type() {
    return (Class<M>) map.getClass();
  }
}
