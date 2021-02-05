package com.oop.intelimenus.placholder;

import com.oop.intelimenus.component.Component;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import lombok.Getter;

public class PlaceholderComponent implements Component<PlaceholderComponent> {

    @Getter
    private final Set<Function<String, String>> placeholders = new HashSet<>();

    @Override
    public PlaceholderComponent clone() {
        PlaceholderComponent component = new PlaceholderComponent();
        component.placeholders.addAll(placeholders);
        return component;
    }
}
