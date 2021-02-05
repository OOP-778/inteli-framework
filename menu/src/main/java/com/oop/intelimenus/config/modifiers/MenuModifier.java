package com.oop.intelimenus.config.modifiers;

import com.oop.inteliframework.config.node.ParentNode;
import com.oop.intelimenus.config.MenuConfiguration;

public abstract class MenuModifier {

    // How does it know about it's sections
    public abstract String getIdentifier();

    // Handle section
    public abstract void handle(ParentNode section, MenuConfiguration configuration);
}
