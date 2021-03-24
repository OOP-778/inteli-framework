package com.oop.inteliframework.config.property.util;

import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.BaseValueNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.property.Configurable;
import com.oop.inteliframework.config.property.InteliPropertyModule;
import com.oop.inteliframework.config.property.annotations.Comment;
import com.oop.inteliframework.config.property.annotations.Exclude;
import com.oop.inteliframework.config.property.annotations.Named;
import com.oop.inteliframework.config.property.annotations.NodeKey;
import com.oop.inteliframework.config.property.property.Property;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.config.property.property.custom.PropertyHandler;
import com.oop.inteliframework.plugin.InteliPlatform;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;

import static com.oop.inteliframework.commons.util.StringFormat.format;
import static com.oop.inteliframework.config.property.util.Helper.isPrimitive;

public class Serializer {
  public static <T> Function<T, SerializedProperty> serializerFor(@NonNull T object) {
    return (Function<T, SerializedProperty>) serializerFor(object.getClass());
  }

  public static <T> Function<T, SerializedProperty> serializerFor(Class<T> clazz) {
    if (isPrimitive(clazz)) return value -> new SerializedProperty(null, new BaseValueNode(value));

    // If it's a possible section
    if (Configurable.class.isAssignableFrom(clazz)) {
      return (Function<T, SerializedProperty>) serializerForConfigurable((Class<Configurable>) clazz, true);
    }

    // Find custom object serializer
    PropertyHandler<T> propertyHandler =
        InteliPlatform.getInstance()
            .safeModuleByClass(InteliPropertyModule.class)
            .handlerByClass(clazz)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        format("Failed to find serializer for {} type object!", clazz)));

    return propertyHandler::toNode;
  }

  public static <T extends Configurable> Function<T, SerializedProperty> serializerForConfigurable(
      Class<T> configurableClass,
      boolean detectKey
  ) {
    // Validation
    InteliPlatform.getInstance()
        .safeModuleByClass(InteliPropertyModule.class)
        .validate(configurableClass);

    LinkedList<Field> propertiesOf = getPropertiesOf(configurableClass);
    return object -> {
      LinkedList<Field> fieldsCopy = new LinkedList<>(propertiesOf);

      BaseParentNode configurableNode = new BaseParentNode();
      String nodeKey = detectKey ? nodeKeyOf(object, fieldsCopy) : null;

      for (Field field : fieldsCopy) {
        try {
          Property o = (Property) field.get(object);
          Named named = field.getAnnotation(Named.class);
          Comment comment = field.getAnnotation(Comment.class);
          Exclude exclude = field.getAnnotation(Exclude.class);

          // If field is excluded continue
          if (exclude != null) {
            continue;
          }

          SerializedProperty serializedProperty = o.toNode();
          String nodeName = serializedProperty.getSuggestedKey() != null ? serializedProperty.getSuggestedKey() : named != null ? named.value() : field.getName();

          if (comment != null) serializedProperty.getNode().appendComments(comment.value());

          configurableNode.nodes().put(nodeName, serializedProperty.getNode());

        } catch (Throwable throwable) {
          throw new IllegalStateException(
              "Failed to handle field with name " + field.getName(), throwable);
        }
      }

      return new SerializedProperty(nodeKey, configurableNode);
    };
  }

  @SneakyThrows
  private static <T extends Configurable> String nodeKeyOf(T object, LinkedList<Field> fields) {
    Named annotation = object.getClass().getAnnotation(Named.class);
    Optional<Field> first =
        fields.stream().filter(field -> field.isAnnotationPresent(NodeKey.class)).findFirst();

    if (first.isPresent()) {
      Field keyField = first.get();
      fields.remove(keyField);

      Object property = keyField.get(object);
      Node node = ((Property) property).toNode().getNode();
      return node.asValue().value().toString();
    }

    return annotation.value();
  }

  public static LinkedList<Field> getPropertiesOf(Class<?> clazz) {
    final LinkedList<Field> fields = new LinkedList<>();
    for (Field field : clazz.getDeclaredFields()) {
      if (!Property.class.isAssignableFrom(field.getType())) {
        continue;
      }

      field.setAccessible(true);
      fields.add(field);
    }
    return fields;
  }
}
