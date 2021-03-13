package com.oop.inteliframework.item;

import com.oop.inteliframework.item.type.InteliLore;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;

@UtilityClass
public class SimpleMetaFactory {
  // Default
  public @NonNull InteliLore emptyLore() {
    return new InteliLore(new ArrayList<>());
  }
}
