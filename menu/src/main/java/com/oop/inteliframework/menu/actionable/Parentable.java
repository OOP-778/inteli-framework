package com.oop.inteliframework.menu.actionable;

import java.util.Optional;

public interface Parentable<T extends Parentable> {

  Optional<T> getParent();

  void setParent(T parent);
}
