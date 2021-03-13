package com.oop.inteliframework.config.property;

import com.oop.inteliframework.config.Configurable;
import com.oop.inteliframework.config.annotations.Comment;
import com.oop.inteliframework.config.annotations.Exclude;
import com.oop.inteliframework.config.annotations.Named;
import com.oop.inteliframework.config.annotations.NodeKey;
import com.oop.inteliframework.config.node.ParentNode;
import com.oop.inteliframework.config.util.ReflectionHelper;
import lombok.NonNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;

import static com.oop.inteliframework.commons.util.StringFormat.format;
import static com.oop.inteliframework.config.util.Helper.apply;

public class PropertyHelper {
  private static void validateClass(Class<? extends Configurable> clazz) {
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
        throw new IllegalStateException(format("class {} contains both a NodeKey & Named annotations, please remove one of them.", clazz.getSimpleName()));
    }
  }

  public void save(@NonNull ParentNode currentParentNode, @NonNull Configurable configurable) {
    validateClass(configurable.getClass());

    // Gather all the properties
    LinkedList<Field> propertiesOf = ReflectionHelper.getPropertiesOf(configurable.getClass());

    Comment header = this.getClass().getAnnotation(Comment.class);
      apply(
              currentParentNode.comments(),
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
        Property o = (Property) field.get(configurable);
        Named named = field.getAnnotation(Named.class);
        Comment comment = field.getAnnotation(Comment.class);
        Exclude exclude = field.getAnnotation(Exclude.class);

        // If field is excluded continue
        if (exclude != null) {
          continue;
        }

        String nodeName = named == null ? field.getName() : named.value();

      } catch (Throwable throwable) {
        throw new IllegalStateException(
            "Failed to handle field with name " + field.getName(), throwable);
      }
    }
  }
}
