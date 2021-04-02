package com.oop.inteliframework.command;

public abstract class ExecutorWrapper {
  public <T extends ExecutorWrapper> T as(Class<T> type) {
    return (T) this;
  }

  public abstract void sendMessage(String text, Object... args);
}
