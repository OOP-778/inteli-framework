package com.oop.inteliframework.config.property;

import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.NodeValuable;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Collection;

import static com.oop.inteliframework.commons.util.CollectionHelper.addAndReturn;
import static com.oop.inteliframework.config.util.Helper.cloneCollection;
import static com.oop.inteliframework.config.util.Helper.isPrimitive;

/**
 * Collection property is used for collections
 * @param <T> The type that list is storing
 * @param <C> the collection type
 */
@AllArgsConstructor
public class CollectionProperty<T, C extends Collection<T>> implements Property<C> {

    @NonNull
    protected C collection;

    @NonNull
    private final Class<T> valueClass;

    public static <T, C extends Collection<T>> CollectionProperty<T, C> from(C collection, Class<T> valueClass, T ...values) {
        return new CollectionProperty<>(addAndReturn(collection, values), valueClass);
    }

    @Override
    public Node toNode(String key) {
        if (isPrimitive(valueClass))
            return new NodeValuable(key, null, collection);

        // TODO: Implement custom object serializers
        throw new IllegalStateException("Custom object serializers not implemented");
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
