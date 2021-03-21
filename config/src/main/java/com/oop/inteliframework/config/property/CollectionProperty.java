package com.oop.inteliframework.config.property;

import com.oop.inteliframework.config.Configurable;
import com.oop.inteliframework.config.InteliConfigModule;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.BaseValueNode;
import com.oop.inteliframework.config.property.custom.PropertyHandler;
import com.oop.inteliframework.plugin.InteliPlatform;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.oop.inteliframework.commons.util.CollectionHelper.addAndReturn;
import static com.oop.inteliframework.commons.util.StringFormat.format;
import static com.oop.inteliframework.config.util.Helper.cloneCollection;
import static com.oop.inteliframework.config.util.Helper.isPrimitive;

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
  public Node toNode(String key) {
    return null;
//    // If value is primitive
//    if (isPrimitive(valueClass)) {
//      return new BaseValueNode(key, null, collection);
//    }
//
//    // If value is a section
//    if (Configurable.class.isAssignableFrom(valueClass)) {
//      BaseParentNode parentNode = new BaseParentNode(key);
//      for (T object : get()) {
//        BaseParentNode objectNode = PropertyHelper.handleConfigurableSerialization(parentNode, (Configurable) object, false);
//        parentNode.nodes().put(objectNode.key(), objectNode);
//      }
//      return parentNode;
//    }
//
//    PropertyHandler<T> handler =
//        InteliPlatform.getInstance()
//            .safeModuleByClass(InteliConfigModule.class)
//            .handlerByClass(valueClass)
//            .orElseThrow(
//                () ->
//                    new IllegalStateException(
//                        format("Failed to find property handler for type {}", valueClass)));
//
//    List<Node> serialized = new LinkedList<>();
//    for (T object : get()) {
//      serialized.add(handler.toNode("unknown", object));
//    }
//
//    // Gotta check if all values of the collection is value nodes if so, this can be just a list
//    if (serialized.stream().allMatch(node -> node instanceof BaseValueNode)) {
//      List<Object> objectList = new LinkedList<>();
//      for (Node node : serialized) {
//        objectList.add(node.asValueSafe().value());
//      }
//
//      return new BaseValueNode(key, objectList);
//    }
//
//    // If we have sections or values mixed, we create new section ;)
//    BaseParentNode parentNode = new BaseParentNode(key, null);
//    for (Node serializedNode : serialized) {
//      parentNode.set(serializedNode.key(), serializedNode);
//    }
//
//    return parentNode;
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
