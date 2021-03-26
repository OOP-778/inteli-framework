package com.oop.inteliframework.menu.menu;

import com.oop.inteliframework.menu.interfaces.Modifier;
import com.oop.inteliframework.menu.trigger.types.ButtonClickTrigger;
import com.oop.inteliframework.menu.menu.simple.InteliMenu;

public interface MenuModifier<T extends InteliMenu> extends Modifier<T> {

  void preClick(ButtonClickTrigger clickTrigger);

  void postClick(ButtonClickTrigger clickTrigger);
}