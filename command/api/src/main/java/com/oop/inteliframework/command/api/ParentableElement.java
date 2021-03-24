package com.oop.inteliframework.command.api;

import com.oop.inteliframework.command.element.BaseElement;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/** This is an element for having children elements for commands/arguments */
@Getter
@Accessors(fluent = true)
public abstract class ParentableElement<P extends ParentableElement<P>> extends BaseElement<P> {

  // A map of children elements
  private final Map<String, CommandElement<?>> children =
      new TreeMap<>(String::compareToIgnoreCase);

  public P addChild(CommandElement<?> element) {
    children.put(element.labeled(), element);
    return (P) this;
  }

  public Optional<CommandElement<?>> findChildrenAt(String path) {
    ParentableElement<?> currentParent = this;
    String[] paths = StringUtils.split(path, ".");

    for (String s : paths) {
        CommandElement<?> element = currentParent.children.get(s);
        if (element instanceof ParentableElement)
            currentParent = (ParentableElement<?>) element;
        else
            return Optional.of(element);
    }

    return Optional.empty();
  }
}
