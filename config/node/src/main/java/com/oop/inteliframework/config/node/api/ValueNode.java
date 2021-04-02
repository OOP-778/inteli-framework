package com.oop.inteliframework.config.node.api;

import java.util.List;

/**
 * An node that holds single object
 */
public interface ValueNode extends Node {

    Object value();

    <T> List<T> getAsListOf(Class<T> type);

    <T> T getAs(Class<T> type);
}
