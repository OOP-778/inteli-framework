package com.oop.inteliframework.animation.provider;

import com.oop.inteliframework.animation.ContentAnimation;

import java.util.Map;

public abstract class AbstractProvider implements AnimationProvider {

    @Override
    public ContentAnimation create(String text, Map<String, Object> options) {
        ContentAnimation animation = new ContentAnimation();
        animation.interval(((Number)options.computeIfAbsent("interval", k -> 200L)).longValue());
        return animation;
    }
}
