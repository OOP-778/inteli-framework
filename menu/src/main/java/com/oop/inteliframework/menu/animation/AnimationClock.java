package com.oop.inteliframework.menu.animation;

import lombok.Setter;

public class AnimationClock {

  @Setter private long interval;
  private long current;

  public AnimationClock(long interval) {
    this.interval = interval;
  }

  public boolean tick() {
    current++;
    if (current == interval) {
      current = 0;
      return true;
    }
    return false;
  }

  public void reset() {
    this.current = 0;
  }
}
