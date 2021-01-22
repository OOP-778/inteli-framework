package com.oop.inteliframework.hologram.animation;

import java.util.TreeMap;

public interface AnimationProvider {
    // Create animation based of content
    ContentAnimation create(String text, TreeMap<String, Object> options);
}
