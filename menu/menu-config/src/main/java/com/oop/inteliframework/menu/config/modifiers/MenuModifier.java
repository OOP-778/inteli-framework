package com.oop.inteliframework.menu.config.modifiers;

import com.oop.inteliframework.config.node.api.ParentNode;
import com.oop.inteliframework.menu.config.MenuConfiguration;

public abstract class MenuModifier {

  // How does it know about it's sections
  public abstract String getIdentifier();

  // Handle section
  public abstract void handle(ParentNode section, MenuConfiguration configuration);
}
