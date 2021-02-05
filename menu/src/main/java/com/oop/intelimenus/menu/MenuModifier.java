package com.oop.intelimenus.menu;

import com.oop.intelimenus.interfaces.Modifier;
import com.oop.intelimenus.menu.simple.InteliMenu;
import com.oop.intelimenus.trigger.types.ButtonClickTrigger;

public interface MenuModifier<T extends InteliMenu> extends Modifier<T> {

    void preClick(ButtonClickTrigger clickTrigger);

    void postClick(ButtonClickTrigger clickTrigger);

}
