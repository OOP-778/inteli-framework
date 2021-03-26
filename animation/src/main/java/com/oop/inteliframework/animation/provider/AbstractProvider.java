package com.oop.inteliframework.animation.provider;

import com.oop.inteliframework.animation.ContentAnimation;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class AbstractProvider implements AnimationProvider {

    @Override
    public ContentAnimation create(String text, Map<String, Object> options) {
        ContentAnimation animation = new ContentAnimation();
        animation.interval((long) options.computeIfAbsent("interval", k -> 200L));
        return animation;
    }
}
