package com.oop.intelimenus.config.modifiers;

import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.intelimenus.config.MenuConfiguration;

public abstract class MenuModifier {

    // How does it know about it's sections
    public abstract String getIdentifier();

    // Handle section
    public abstract void handle(BaseParentNode section, MenuConfiguration configuration);
}
