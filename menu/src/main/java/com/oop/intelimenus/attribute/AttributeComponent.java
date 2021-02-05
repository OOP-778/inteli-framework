package com.oop.intelimenus.attribute;

import com.oop.intelimenus.button.ButtonModifier;
import com.oop.intelimenus.button.IButton;
import com.oop.intelimenus.component.Component;
import com.oop.intelimenus.component.ComponentHolder;
import com.oop.intelimenus.interfaces.Modifier;
import com.oop.intelimenus.menu.MenuModifier;
import com.oop.intelimenus.menu.simple.InteliMenu;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;

public class AttributeComponent implements Component<AttributeComponent> {

    private final Set<Attribute> attributes = new HashSet<>();
    private ComponentHolder holder;

    public void addAttribute(@NonNull Attribute attribute) {
        removeAttribute(attribute);
        processAttribute(attribute);
        attributes.add(attribute);
    }

    public void removeAttribute(@NonNull Attribute attribute) {
        attributes.remove(attribute);
    }

    public boolean hasAttribute(@NonNull Attribute attribute) {
        return attributes.stream()
            .anyMatch(attribute2 -> attribute2.getId().equalsIgnoreCase(attribute.getId()));
    }

    @Override
    public AttributeComponent clone() {
        AttributeComponent component = new AttributeComponent();
        component.attributes.addAll(attributes);
        return component;
    }

    @Override
    public void onAdd(ComponentHolder holder) {
        this.holder = holder;
        Class<? extends Modifier> clazz = getHolderTypeClass();
//        attributes
//                .stream()
//                .filter(attribute -> Arrays.asList(attribute.getClass().getInterfaces()).contains(clazz))
//                .forEach(attribute -> ((Modifier) attribute).onAdd(holder));
    }

    private void processAttribute(Attribute attribute) {
        if (holder == null) {
            return;
        }

        Class<? extends Modifier> holderClass = getHolderTypeClass();
        if (Arrays.asList(attribute.getClass().getInterfaces()).contains(holderClass)) {
            ((Modifier) attribute).onAdd(holder);
        }
    }

    private Class<? extends Modifier> getHolderTypeClass() {
        if (holder == null) {
            return null;
        }

        Class<? extends Modifier> clazz = null;
        if (holder instanceof InteliMenu) {
            clazz = MenuModifier.class;
        } else if (holder instanceof IButton) {
            clazz = ButtonModifier.class;
        }
        return clazz;
    }

    @Override
    public String toString() {
        return "AttributeComponent{" +
            "attributes=" + Arrays.toString(attributes.stream().map(Attribute::getId).toArray()) +
            '}';
    }
}
