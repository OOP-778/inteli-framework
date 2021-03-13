package com.oop.inteliframework.config.property;

import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.config.Configurable;
import com.oop.inteliframework.config.annotations.Comment;
import com.oop.inteliframework.config.annotations.Exclude;
import com.oop.inteliframework.config.annotations.Named;
import com.oop.inteliframework.config.annotations.NodeKey;
import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.ParentNode;
import com.oop.inteliframework.config.node.ValueNode;
import com.oop.inteliframework.config.util.ReflectionHelper;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

import static com.oop.inteliframework.commons.util.StringFormat.format;
import static com.oop.inteliframework.config.util.Helper.apply;

public class PropertyHelper {
  @SneakyThrows
  private static void validateClass(
      Configurable configurable, Class<? extends Configurable> clazz) {
    // Double key check
    LinkedList<Field> propertiesOf = ReflectionHelper.getPropertiesOf(clazz);

    Field nodeKeyField = null;

    for (Field field : propertiesOf) {
      if (field.isAnnotationPresent(NodeKey.class)) {
        nodeKeyField = field;
        break;
      }
    }

    if (nodeKeyField != null && clazz.isAnnotationPresent(Named.class)) {
      throw new IllegalStateException(
          format(
              "class {} contains both a NodeKey & Named annotations, please remove one of them.",
              clazz.getSimpleName()));
    }

    // Properties amount check
    Preconditions.checkArgument(
            !propertiesOf.stream().allMatch(field -> field.isAnnotationPresent(NodeKey.class)),
            format("Failed to find at least single property in class {}", clazz)
    );

    // Property key check
    for (Field field : propertiesOf) {
      if (field.isAnnotationPresent(NodeKey.class)) continue;

      if (!field.isAnnotationPresent(Named.class)) {
        if (!Configurable.class.isAssignableFrom(((Property) field.get(configurable)).type()))
          throw new IllegalStateException(
              format(
                  "A field of name '{}' in class {} doesn't have a @Named annotation neither it's configurable",
                  field.getName(),
                  clazz.getSimpleName()));
      }
    }
  }

  public static ParentNode handleConfigurableSerialization(
      @Nullable ParentNode parent,
      @NonNull Configurable configurable,
      boolean useParentAsConfigurableNode
  ) {
    validateClass(configurable, configurable.getClass());

    // Gather all the properties
    LinkedList<Field> propertiesOf = ReflectionHelper.getPropertiesOf(configurable.getClass());

    ParentNode configurableNode = null;
    if (!useParentAsConfigurableNode) {
      Named annotation = configurable.getClass().getAnnotation(Named.class);
      Optional<Field> first =
              propertiesOf.stream()
                      .filter(field -> field.isAnnotationPresent(NodeKey.class))
                      .findFirst();

      if (annotation != null) {
        configurableNode = new ParentNode(annotation.value(), parent);
      }

      if (first.isPresent()) {
        try {
          Property nodeKeyProperty = (Property) first.get().get(configurable);
          Node key = nodeKeyProperty.toNode("key");
          Preconditions.checkArgument(
                  key instanceof ValueNode, format("Cannot set node key as {} type", key.getClass()));

          configurableNode = new ParentNode(((ValueNode) key).value().toString(), parent);

        } catch (Throwable throwable) {
          throw new IllegalStateException(
                  format("Invalid NodeKey at class {}", configurable.getClass().getSimpleName()),
                  throwable);
        }
      }
    } else
      configurableNode = parent;

    Preconditions.checkArgument(
            configurableNode != null,
            "Failed to define configurable node, cause either invalid key or none present");

    Comment header = configurable.getClass().getAnnotation(Comment.class);
    apply(
        configurableNode.comments(),
        (comments) -> {
          if (header.override()) {
            comments.clear();
          }

          if (comments.isEmpty()) {
            comments.addAll(Arrays.asList(header.value()));
          }
        });

    for (Field field : propertiesOf) {
      try {
        // If the field is NodeKey we ignore it!
        if (field.isAnnotationPresent(NodeKey.class)) continue;

        Property o = (Property) field.get(configurable);
        Named named = field.getAnnotation(Named.class);
        Comment comment = field.getAnnotation(Comment.class);
        Exclude exclude = field.getAnnotation(Exclude.class);

        // If field is excluded continue
        if (exclude != null) {
          continue;
        }

        String nodeName = named == null ? field.getName() : named.value();
        Node node = o.toNode(nodeName);

        if (comment != null) node.appendComments(comment.value());

        configurableNode.nodes().put(nodeName, node);

      } catch (Throwable throwable) {
        throw new IllegalStateException(
            "Failed to handle field with name " + field.getName(), throwable);
      }
    }

    return configurableNode;
  }
}
