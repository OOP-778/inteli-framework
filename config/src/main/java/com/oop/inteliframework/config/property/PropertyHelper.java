package com.oop.inteliframework.config.property;

import static com.oop.inteliframework.config.util.Helper.apply;

import com.oop.inteliframework.config.annotations.Comment;
import com.oop.inteliframework.config.annotations.Exclude;
import com.oop.inteliframework.config.annotations.Named;
import com.oop.inteliframework.config.node.ParentNode;
import com.oop.inteliframework.config.util.ReflectionHelper;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import lombok.NonNull;

public class PropertyHelper {

    public void save(
        @NonNull ParentNode currentParentNode,
        @NonNull Object object
    ) {
        // Gather all the properties
        LinkedList<Field> propertiesOf = ReflectionHelper
            .getPropertiesOf(this.getClass());

        Comment header = this.getClass().getAnnotation(Comment.class);
        if (header.override()) {
            apply(currentParentNode.comments(), (comments) -> {
                comments.clear();
                comments.addAll(Arrays.asList(header.value()));
            });
        }

        for (Field field : propertiesOf) {
            try {
                Property o = (Property) field.get(object);
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
