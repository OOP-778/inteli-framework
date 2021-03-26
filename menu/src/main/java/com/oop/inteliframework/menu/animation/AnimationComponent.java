package com.oop.inteliframework.menu.animation;

import com.oop.inteliframework.menu.component.Component;
import com.oop.inteliframework.menu.component.ComponentHolder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AnimationComponent implements Component<AnimationComponent> {

  private final Set<Animation> animationSet = new HashSet<>();
  private ComponentHolder holder;

  @Override
  public AnimationComponent clone() {
    AnimationComponent component = new AnimationComponent();
    component.animationSet.addAll(
        animationSet.stream().map(Animation::clone).collect(Collectors.toSet()));
    return component;
  }

  public void add(Animation animation) {
    animationSet.add(animation);
  }

  @Override
  public void onAdd(ComponentHolder holder) {
    this.holder = holder;
  }

  public boolean hasAnimations() {
    return !animationSet.isEmpty();
  }

  public void execute() {
    animationSet.forEach(animation -> animation.execute(holder));
  }
}
