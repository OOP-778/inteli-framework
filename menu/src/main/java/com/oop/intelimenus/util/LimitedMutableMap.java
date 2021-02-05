package com.oop.intelimenus.util;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LimitedMutableMap<K, V> implements Map<K, V> {

    private final Map<K, V> map;
    private final boolean supportsRemoving;
    private final boolean supportsAdding;
    private boolean locked = false;

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        Preconditions.checkArgument(supportsAdding && !isLocked(), "Adding is not supported!");
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        Preconditions.checkArgument(supportsRemoving, "Removing is not supported!");
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public void lock() {
        locked = true;
    }

    public boolean isLocked() {
        return locked;
    }
}
