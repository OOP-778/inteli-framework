package com.oop.inteliframework.config.node;

import com.google.common.primitives.Primitives;
import com.oop.inteliframework.commons.util.Preconditions;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Accessors(fluent = true)
public class NodeValuable extends BaseNode {
    @NonNull
    @Getter
    private final Object value;

    public NodeValuable(String key, @Nullable ParentableNode parent, @NonNull Object value) {
        super(key, parent);
        this.value = value;
    }

    @Override
    public boolean isParentable() {
        return false;
    }

    @Override
    public Optional<ParentableNode> asParentable() {
        return Optional.empty();
    }

    @Override
    public Optional<NodeValuable> asValuable() {
        return Optional.of(this);
    }

    public <T> List<T> getAsListOf(Class<T> type) {
        Preconditions.checkArgument(value instanceof List, "The value is not a list, but " + value.getClass().getSimpleName());

        List<T> typedList = new LinkedList<>();
        List listObject = (List) value;
        for (Object o : listObject)
            typedList.add(getAs(o, type));

        return typedList;
    }

    public <T> T getAs(Class<T> clazz) {
        return getAs(value, clazz);
    }

    private <T> T getAs(Object object, Class<T> clazz) {
        Class objectClazz = Primitives.unwrap(value.getClass());
        Class requiredClazz = Primitives.unwrap(clazz);

        // If the required class is the one that the value is, just return it
        if (objectClazz == requiredClazz)
            return (T) value;

        return (T) doConversion(value, requiredClazz);
    }

    private <T> Object doConversion(Object parsed, Class<T> clazz) {
        String value = parsed.toString();
        if (clazz == String.class) return value;

        if ((parsed.getClass() == Double.class || parsed.getClass() == Float.class && clazz == Integer.class)) {
            if (value.contains(".")) {
                String[] splitValue = value.split("\\.");
                int original = Integer.parseInt(splitValue[0]);
                int secondPart = splitValue[1].toCharArray()[0];

                if (secondPart > 4)
                    original += 1;

                value = Integer.toString(original);
            }
        }

        if (clazz == Integer.class)
            return Integer.valueOf(value);

        else if (clazz == Long.class)
            return Long.valueOf(value);

        else if (clazz == Float.class)
            return Float.valueOf(value);

        else if (clazz == Double.class)
            return Double.valueOf(value);

        else
            throw new IllegalStateException("Incorrect object type required: " + clazz.getSimpleName() + " found: " + parsed.getClass().getSimpleName());
    }
}
