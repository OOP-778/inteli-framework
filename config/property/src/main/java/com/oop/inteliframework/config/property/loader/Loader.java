package com.oop.inteliframework.config.property.loader;

import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.config.node.BaseValueNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.ParentNode;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import com.oop.inteliframework.config.property.Configurable;
import com.oop.inteliframework.config.property.InteliPropertyModule;
import com.oop.inteliframework.config.property.annotations.Named;
import com.oop.inteliframework.config.property.annotations.NodeKey;
import com.oop.inteliframework.config.property.annotations.Optional;
import com.oop.inteliframework.config.property.property.Property;
import com.oop.inteliframework.config.property.property.custom.PropertyHandler;
import com.oop.inteliframework.plugin.InteliPlatform;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import static com.oop.inteliframework.commons.util.StringFormat.format;

public class Loader {
  public static <T> Function<Node, T> loaderFrom(T object) {
    return loaderFrom((Class<T>) object.getClass());
  }

  public static <T> Function<Node, T> loaderFrom(Class<T> clazz) {
    if (BaseValueNode.isPrimitive(clazz)) {
      return node -> node.asValue().getAs(clazz);
    }

    if (Configurable.class.isAssignableFrom(clazz)) {
      return loaderFromConfigurable((Class<? extends Configurable>) clazz);
    }

    // Find custom object serializer
    PropertyHandler<T> propertyHandler =
        InteliPlatform.getInstance()
            .safeModuleByClass(InteliPropertyModule.class)
            .handlerByClass(clazz)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        format("Failed to find loader for {} type object!", clazz)));

    return propertyHandler::fromNode;
  }

  public static <T> Function<Node, T> loaderFromConfigurable(
      Class<? extends Configurable> clazz) {
    // Validation
    InteliPlatform.getInstance().safeModuleByClass(InteliPropertyModule.class).validate(clazz);

    Constructor<T> constructor;
    try {
      constructor = (Constructor<T>) clazz.getDeclaredConstructor();
      constructor.setAccessible(true);
    } catch (Throwable throwable) {
      throw new IllegalStateException("Failed to find empty constructor for class: " + clazz);
    }

    List<Field> propertiesOf =
        InteliPlatform.getInstance()
            .safeModuleByClass(InteliPropertyModule.class)
            .getClassesCache()
            .getFields(clazz);

    final TreeMap<String, Field> mappedProperties = new TreeMap<>(String::compareToIgnoreCase);

    for (Field field : propertiesOf) {
      if (field.getAnnotation(NodeKey.class) != null) continue;
      Named namedAnnotation = field.getAnnotation(Named.class);
      mappedProperties.put(namedAnnotation.value(), field);
    }

    propertiesOf.clear();

    return node -> {
      Preconditions.checkArgument(
          node instanceof ParentNode, "Configurable node requires an ParentNode and it's not");

      T object;
      try {
        object = constructor.newInstance();
      } catch (Throwable throwable) {
        throw new IllegalStateException(
            "Failed to initialize object of class: " + clazz, throwable);
      }

      ParentNode configurableNode = (ParentNode) node;
      Map<String, Node> nodesMap = configurableNode.map(NodeIterator.ALL);

      for (Map.Entry<String, Field> objectPropertyEntry : mappedProperties.entrySet()) {
        try {
          Node entryNode = nodesMap.get(objectPropertyEntry.getKey());
          if (entryNode == null) {
            if (objectPropertyEntry.getValue().getAnnotation(Optional.class) != null) {
              continue;
            }

            throw new IllegalStateException(
                format(
                    "Failed to find required value of class {} value: {}",
                    clazz,
                    objectPropertyEntry.getKey()));
          }

          Property entryProperty = (Property) objectPropertyEntry.getValue().get(object);
          entryProperty.fromNode(entryNode);

        } catch (Throwable throwable) {
          throw new IllegalStateException(
              format("Failed to handle class {} property {}", clazz, objectPropertyEntry.getKey()),
              throwable);
        }
      }

      return object;
    };
  }
}
