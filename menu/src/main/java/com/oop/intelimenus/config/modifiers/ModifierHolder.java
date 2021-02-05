package com.oop.intelimenus.config.modifiers;

import com.oop.intelimenus.config.modifiers.def.ActionModifier;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

public class ModifierHolder {

    @Getter
    private final Map<Class, MenuModifier> modifierMap = new HashMap<>();

    public ModifierHolder() {
        registerModifier(new ActionModifier());
    }

    public void registerModifier(MenuModifier modifier) {
        modifierMap.put(modifier.getClass(), modifier);
    }

    public <T extends MenuModifier> T getModifier(Class<T> clazz) {
        return (T) modifierMap.get(clazz);
    }

    public <T extends MenuModifier> T getModifier(String name) {
        return (T) modifierMap.values().stream()
            .filter(am -> am.getIdentifier().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }
}
