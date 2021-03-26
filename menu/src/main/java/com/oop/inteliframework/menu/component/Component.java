package com.oop.inteliframework.menu.component;

public interface Component<C extends Component<C>> extends Cloneable {

  C clone();

  default void onAdd(ComponentHolder holder) {}
}
