package com.oop.inteliframework.config.property;

import com.oop.inteliframework.config.node.Node;

/**
 * The base interface of any property
 *
 * @param <T> the type of object that this property holds
 */
public interface Property<T> {

    /**
     * Convert property to node
     *
     * @param key key of the node
     * @return a converted node from property
     */
    Node toNode(String key);

    /**
     * Get the property value
     *
     * @return property value
     */
    T get();

    /**
     * Type of the data that this property holds
     *
     * @return Class of the type
     */
    Class<T> type();
}
