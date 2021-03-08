package com.oop.inteliframework.commons.util;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class InteliCache<K, V> {

    @Getter
    private final int concurrencyLevel;

    private final long expireAfter;

    private final boolean resetExpireAfterAccess;

    // Key, Value with when it expires
    private final Map<K, InteliPair<V, Long>> data;

    InteliCache(Builder builder) {
        this.concurrencyLevel = builder.concurrencyLevel;
        this.expireAfter = builder.expireAfter;
        this.resetExpireAfterAccess = builder.resetExpireAfterAccess;

        if (concurrencyLevel == 0)
            data = new HashMap<>();
        else
            data = new ConcurrentHashMap<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public V get(K key) {
        InteliPair<V, Long> pair = _get(key);
        return pair == null ? null : pair.getKey();
    }

    public void clear() {
        data.clear();
    }

    public void remove(K key) {
        data.remove(key);
    }

    public void put(K key, V value) {
        checkForInvalids();

        data.remove(key);
        data.put(key, new InteliPair<>(value, System.currentTimeMillis() + expireAfter));
    }

    public V getIfAbsent(K key, Supplier<V> supplier) {
        V value = get(key);
        if (value == null) {
            value = supplier.get();
            put(key, value);
        }

        return value;
    }

    public void replace(K key, V value) {
        InteliPair<V, Long> v = _get(key);
        if (v == null)
            put(key, value);
        else
            v.setKey(value);
    }

    public V merge(K key, V value, BiFunction<V, V, V> merger) {
        InteliPair<V, Long> pair = _get(key);
        if (pair == null) {
            put(key, value);
            return value;
        }

        pair.setKey(merger.apply(pair.getKey(), value));
        return pair.getKey();
    }

    public Set<K> keys() {
        return data.keySet();
    }

    private void checkForInvalids() {
        if (expireAfter == -1) return;
        data.entrySet().removeIf(entry -> entry.getValue().getValue() <= System.currentTimeMillis());
    }

    private InteliPair<V, Long> _get(K key) {
        checkForInvalids();

        InteliPair<V, Long> valuePair = data.get(key);
        if (valuePair != null && resetExpireAfterAccess)
            valuePair.setValue(System.currentTimeMillis() + expireAfter);

        return valuePair;
    }

    public boolean has(K key) {
        return data.containsKey(key);
    }

    @Accessors(chain = true, fluent = true)
    public static class Builder {
        // Level 0 = Sync Use Only, level 1 = Full Multi Thread support
        @Setter
        private int concurrencyLevel = 0;
        @Setter
        private boolean resetExpireAfterAccess = false;

        // How long will the values be in map
        @Setter
        private long expireAfter = -1;

        public Builder() {
        }

        public Builder expireAfter(long time, TimeUnit unit) {
            this.expireAfter = unit.toMillis(time);
            return this;
        }

        public <K, V> InteliCache<K, V> build() {
            return new InteliCache<>(this);
        }
    }
}
