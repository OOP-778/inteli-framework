package com.oop.inteliframework.animation.provider;

import com.oop.inteliframework.animation.ContentAnimation;

import java.util.Map;

public interface AnimationProvider {
  // Create animation based of content
  ContentAnimation create(String text, Map<String, Object> options);
}
