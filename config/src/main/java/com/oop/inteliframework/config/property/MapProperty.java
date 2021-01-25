package com.oop.inteliframework.config.property;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.NodeValuable;
import com.oop.inteliframework.config.node.ParentableNode;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Map;
import java.util.Set;

import static com.oop.inteliframework.config.util.Helper.*;

@AllArgsConstructor
public class MapProperty<K, V, M extends Map> implements Property<M> {

    @NonNull
    private final Class<K> keyClass;

    @NonNull
    private final Class<V> valueClass;

    @NonNull
    protected M map;

    @Override
    public Node toNode(String key) {
        ParentableNode node = new ParentableNode(key, null);
        if (isPrimitive(keyClass) && isPrimitive(valueClass)) {
            for (Map.Entry entry : (Set<Map.Entry<K, V>>) map.entrySet()) {
                node.nodes()
                        .put(entry.getKey().toString(), new NodeValuable(entry.getKey().toString(), null, entry.getValue()));
            }
            return node;
        }

        // TODO: Implement custom object serializers
        throw new IllegalStateException("Custom object serializers not implemented");
    }

    @Override
    public M get() {
        return (M) cloneMap((Map<K, V>)map);
    }

    @Override
    public Class<M> type() {
        return (Class<M>) map.getClass();
    }

    public static <K, V, M extends Map<K, V>> MapProperty<K, V, M> from(M map, Class<K> keyClass, Class<V> valueClass, InteliPair<K, V> ...entries) {
        return new MapProperty<>(
                keyClass,
                valueClass,
                use(map, ()-> {
                    for (InteliPair<K, V> entry : entries) {
                        map.put(entry.getKey(), entry.getValue());
                    }
                })
        );
    }
}
