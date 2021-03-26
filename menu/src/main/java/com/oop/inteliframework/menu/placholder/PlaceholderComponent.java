package com.oop.inteliframework.menu.placholder;

import com.oop.inteliframework.menu.component.Component;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class PlaceholderComponent implements Component<PlaceholderComponent> {

  @Getter private final Set<Function<String, String>> placeholders = new HashSet<>();

  @Override
  public PlaceholderComponent clone() {
    PlaceholderComponent component = new PlaceholderComponent();
    component.placeholders.addAll(placeholders);
    return component;
  }
}
