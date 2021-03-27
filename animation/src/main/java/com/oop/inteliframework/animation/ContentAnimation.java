package com.oop.inteliframework.animation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(fluent = true, chain = true)
@ToString
public class ContentAnimation {

    // All of the frames
    @Getter
    @Setter
    private List<String> frames;

    // How often should it run
    @Getter
    @Setter
    private long interval;

    @Setter(AccessLevel.PROTECTED)
    @Getter
    private long runAt = -1;

    // Current frame
    private int currentFrame = 0;

    public String currentFrame() {
        return frames.get(currentFrame);
    }

    public void next() {
        currentFrame++;
        if (frames.size() == currentFrame)
            currentFrame = 0;
    }
}
