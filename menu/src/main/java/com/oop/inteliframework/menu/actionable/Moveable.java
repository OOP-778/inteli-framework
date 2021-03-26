package com.oop.inteliframework.menu.actionable;

import java.util.Optional;

public interface Moveable<T extends Moveable> {

  /*
  Set current moving object
  */
  void setMoving(T where);

  /*
  Get current moving object
  */
  Optional<T> getCurrentMoving();
}
