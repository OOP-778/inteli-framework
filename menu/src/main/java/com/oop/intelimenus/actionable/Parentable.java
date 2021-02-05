package com.oop.intelimenus.actionable;

import java.util.Optional;

public interface Parentable<T extends Parentable> {

    Optional<T> getParent();

    void setParent(T parent);
}
