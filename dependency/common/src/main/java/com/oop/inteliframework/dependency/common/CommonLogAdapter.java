package com.oop.inteliframework.dependency.common;

import com.oop.inteliframework.dependency.logging.LogLevel;
import com.oop.inteliframework.dependency.logging.adapters.LogAdapter;

public class CommonLogAdapter implements LogAdapter {
  @Override
  public void log(LogLevel level, String message) {
    System.out.println(message);
  }

  @Override
  public void log(LogLevel level, String message, Throwable throwable) {
    System.out.println(message);
    throwable.printStackTrace();
  }
}
