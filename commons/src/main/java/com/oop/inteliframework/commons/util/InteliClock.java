package com.oop.inteliframework.commons.util;

import lombok.Getter;

public class InteliClock {

  @Getter private long currentMillis;
  private long runAt;
  private boolean firstTime = true;

  public InteliClock(long runAt) {
    this.runAt = runAt;
  }

  public boolean tick() {
    if (firstTime) {
      firstTime = false;
      return true;
    }

    currentMillis++;
    if (runAt <= currentMillis) {
      reset();
      return true;
    }
    return false;
  }

  public void setRunAt(long runAt) {
    this.runAt = runAt;
    reset();
  }

  public void reset() {
    currentMillis = 0;
  }
}
