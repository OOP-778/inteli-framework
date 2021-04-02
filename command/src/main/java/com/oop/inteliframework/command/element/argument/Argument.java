package com.oop.inteliframework.command.element.argument;

import com.oop.inteliframework.command.api.ParentableElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/** Command element for Argument */
@Accessors(chain = true, fluent = true)
public class Argument<T> extends ParentableElement<Argument<T>> {

  /** Used for parsing argument into object */
  @Setter @Getter @NonNull private ArgumentParser<T> parser;

  /** If the argument should be optional */
  @Getter @Setter private boolean optional = false;
}
