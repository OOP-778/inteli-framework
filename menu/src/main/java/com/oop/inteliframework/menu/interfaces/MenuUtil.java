package com.oop.inteliframework.menu.interfaces;

public interface MenuUtil {
  void ensureSync(Runnable runnable);

  void async(Runnable runnable);

  void playSound(String name, float volume, float pitch, float yaw);
}
