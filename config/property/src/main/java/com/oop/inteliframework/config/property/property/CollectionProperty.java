package com.oop.inteliframework.config.property.property;

import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.BaseValueNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.ValueNode;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import com.oop.inteliframework.config.property.Configurable;
import com.oop.inteliframework.config.property.InteliPropertyModule;
import com.oop.inteliframework.config.property.annotations.NodeKey;
import com.oop.inteliframework.plugin.InteliPlatform;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

import static com.oop.inteliframework.commons.util.CollectionHelper.addAndReturn;
import static com.oop.inteliframework.config.property.loader.Loader.loaderFrom;
import static com.oop.inteliframework.config.property.serializer.Serializer.serializerFor;
import static com.oop.inteliframework.config.property.util.Helper.cloneCollection;

/**
 * Collection property is used for collections
 *
 * @param <T> The type that list is storing
 * @param <C> the collection type
 */
@AllArgsConstructor
@ToString
public class CollectionProperty<T, C extends Collection<T>> implements Property<C> {

  @NonNull private final Class<T> valueClass;
  @NonNull protected C collection;

  public static <T, C extends Collection<T>> CollectionProperty<T, C> from(
      C collection, Class<T> valueClass, T... values) {
    return new CollectionProperty<>(valueClass, addAndReturn(collection, values));
  }

  @Override
  public void fromNode(Node node) {
    Function<Node, T> valueLoader = loaderFrom(valueClass);

    if (node instanceof BaseParentNode) {
      for (Map.Entry<String, Node> nodeEntry :
          ((BaseParentNode) node).map(NodeIterator.ALL).entrySet()) {

        T value = valueLoader.apply(nodeEntry.getValue());
        if (Configurable.class.isAssignableFrom(valueClass)) {
          List<Field> propertiesOf =
              InteliPlatform.getInstance()
                  .safeModuleByClass(InteliPropertyModule.class)
                  .getClassesCache()
                  .getFields(value.getClass());

          Optional<Field> nodeKeyField =
              propertiesOf.stream()
                  .filter(field -> field.getAnnotation(NodeKey.class) != null)
                  .findFirst();

          nodeKeyField.ifPresent(
              field -> {
                try {
                  Object o = field.get(this);
                  ((Property) o).fromNode(new BaseValueNode(nodeEntry.getKey()));
                } catch (Throwable t) {
                  t.printStackTrace();
                }
              });
        }
        collection.add(value);
      }
      return;
    }

    for (Object o : ((List) node.asValue().value())) {
      collection.add(new BaseValueNode(o).getAs(valueClass));
    }
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
      parentNode.assignNode(
          Objects.requireNonNull(
              serializedNode.getSuggestedKey(), "Suggested key is required, but not found :/"),
          serializedNode.getNode());
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
