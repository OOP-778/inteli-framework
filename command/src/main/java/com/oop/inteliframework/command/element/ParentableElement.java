package com.oop.inteliframework.command.element;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.TreeMap;

/**
 * This is an element for having children elements for commands/arguments
 */
@Getter
@Accessors(fluent = true)
public abstract class ParentableElement<P extends ParentableElement<P>> extends BaseElement<P> {

    // A map of children elements
    private final Map<String, CommandElement<?>> children = new TreeMap<>(String::compareToIgnoreCase);

    public P addChild(CommandElement<?> element) {
        children.put(element.labeled(), element);
        return (P) this;
    }
}
