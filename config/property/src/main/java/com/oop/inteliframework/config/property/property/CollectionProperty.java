package com.oop.inteliframework.config.property.property;

import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.BaseValueNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.ValueNode;
import com.oop.inteliframework.config.property.Configurable;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.oop.inteliframework.commons.util.CollectionHelper.addAndReturn;
import static com.oop.inteliframework.config.property.util.Helper.cloneCollection;
import static com.oop.inteliframework.config.property.util.Serializer.serializerFor;

/**
 * Collection property is used for collections
 *
 * @param <T> The type that list is storing
 * @param <C> the collection type
 */
@AllArgsConstructor
public class CollectionProperty<T, C extends Collection<T>> implements Property<C> {

  @NonNull private final Class<T> valueClass;
  @NonNull protected C collection;

  public static <T, C extends Collection<T>> CollectionProperty<T, C> from(
      C collection, Class<T> valueClass, T... values) {
    return new CollectionProperty<>(valueClass, addAndReturn(collection, values));
  }

  @Override
  public SerializedProperty toNode() {
    Function<T, SerializedProperty> serializer = serializerFor(valueClass);
    List<SerializedProperty> serialized = new LinkedList<>();

    for (T object : get()) {
      serialized.add(serializer.apply(object));
    }

    // Gotta check if all values of the collection is value nodes if so, this can be just a list
    if (serialized.stream().allMatch(node -> node.getNode() instanceof ValueNode)) {
      List<Object> objectList = new LinkedList<>();
      for (SerializedProperty node : serialized) {
        objectList.add(node.getNode().asValue().value());
      }

      return SerializedProperty.of(new BaseValueNode(objectList));
    }

    // If we have sections or values mixed, we create new section ;)
    BaseParentNode parentNode = new BaseParentNode();
    for (SerializedProperty serializedNode : serialized) {
      parentNode.assignNode(Objects.requireNonNull(serializedNode.getSuggestedKey(), "Suggested key is required, but not found :/"), serializedNode.getNode());
    }

    return SerializedProperty.of(parentNode);
  }

  @Override
  public C get() {
    return cloneCollection(collection);
  }

  @Override
  public Class<C> type() {
    return (Class<C>) collection.getClass();
  }

  public Class<T> valueClass() {
    return valueClass;
  }
}
