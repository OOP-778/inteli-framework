package com.oop.intelimenus.component;

public interface Component<C extends Component<C>> extends Cloneable {

    C clone();

    default void onAdd(ComponentHolder holder) {
    }
}
