package com.oop.intelimenus.interfaces;

import com.oop.intelimenus.component.ComponentHolder;
import java.util.Optional;

public interface MenuSlot<T extends MenuSlot<T, B>, B extends MenuButton<B>> extends Cloneable,
    ComponentHolder<T> {

    /*
        Button that holds this slot
        */
    Optional<B> getHolder();

    /*
    Set slot holder
    */
    void setHolder(B button);

    /*
    Slot of the inventory
    */
    int getIndex();

    /*
    Set slot index
    */
    void setIndex(int index);

    /*
    Clone the slot
    */
    T clone();
}
