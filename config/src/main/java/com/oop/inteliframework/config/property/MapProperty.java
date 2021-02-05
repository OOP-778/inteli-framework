package com.oop.inteliframework.config.property;

import static com.oop.inteliframework.config.util.Helper.cloneMap;
import static com.oop.inteliframework.config.util.Helper.isPrimitive;
import static com.oop.inteliframework.config.util.Helper.use;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.ValueNode;
import com.oop.inteliframework.config.node.ParentNode;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class MapProperty<K, V, M extends Map> implements Property<M> {

    @NonNull
    private final Class<K> keyClass;

    @NonNull
    private final Class<V> valueClass;

    @NonNull
    protected M map;

    public static <K, V, M extends Map<K, V>> MapProperty<K, V, M> from(M map, Class<K> keyClass,
        Class<V> valueClass, InteliPair<K, V>... entries) {
        return new MapProperty<>(
            keyClass,
            valueClass,
            use(map, () -> {
                for (InteliPair<K, V> entry : entries) {
                    map.put(entry.getKey(), entry.getValue());
                }
            })
        );
    }

    @Override
    public Node toNode(String key) {
        ParentNode node = new ParentNode(key, null);
        if (isPrimitive(keyClass) && isPrimitive(valueClass)) {
            for (Map.Entry entry : (Set<Map.Entry<K, V>>) map.entrySet()) {
                node.nodes()
                    .put(entry.getKey().toString(),
                        new ValueNode(entry.getKey().toString(), null, entry.getValue()));
            }
            return node;
        }

        // TODO: Implement custom object serializers
        throw new IllegalStateException("Custom object serializers not implemented");
    }

    @Override
    public M get() {
        return (M) cloneMap((Map<K, V>) map);
    }

    @Override
    public Class<M> type() {
        return (Class<M>) map.getClass();
    }
}
