package com.oop.inteliframework.animation;

import com.oop.inteliframework.animation.provider.AnimationProvider;
import com.oop.inteliframework.animation.provider.EraseAnimation;
import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.Getter;

import java.util.TreeMap;

@Getter
public class InteliAnimationModule implements InteliModule {
  private final TreeMap<String, AnimationProvider> providerTreeMap =
      new TreeMap<>(String::compareToIgnoreCase);

  public InteliAnimationModule() {
    registerProvider("erase", new EraseAnimation());
  }

  public void registerProvider(String name, AnimationProvider provider) {
    providerTreeMap.put(name, provider);
  }
}
