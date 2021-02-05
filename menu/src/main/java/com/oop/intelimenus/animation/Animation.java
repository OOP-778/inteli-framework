package com.oop.intelimenus.animation;

import com.oop.intelimenus.util.TriConsumer;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
public class Animation<T> implements Cloneable {

    // What's the current frame
    private int currentFrame = 0;

    // How much frames there are
    @Setter
    @Getter
    private int frames;

    /*
    if set to false, after last frame is done, it will instantly go back to frame 0
    If set to true, after last frame is done, it will go back to frame 0 gradually
    */
    @Setter
    @Getter
    private boolean shouldGoBack = false;

    @Setter
    @Getter
    // Will the animation repeat
    private boolean repeat = false;

    @Setter
    @Getter
    // Interval on how fast the animation will run
    private long interval;

    @Getter
    // A boolean to check if the animation has ended
    private boolean finished;

    @Setter
    // When frame is happening
    private @NonNull TriConsumer<Integer, Animation<T>, T> onFrame;

    @Getter
    private boolean isGoingBack = false;

    private AnimationClock clock;

    public void execute(T object) {
        if (finished) {
            return;
        }

        if (clock == null) {
            clock = new AnimationClock(interval);
        }

        // If clock hasn't reached it's interval stop
        if (!clock.tick()) {
            return;
        }

        if (isGoingBack) {
            currentFrame--;
            if (currentFrame == 0) {
                isGoingBack = false;
                if (!repeat) {
                    finished = true;
                }

                currentFrame += 2;
            }
        } else {
            currentFrame++;
        }

        // Check if the animation has finished
        if (currentFrame == frames) {
            if (shouldGoBack) {
                isGoingBack = true;
            } else if (!repeat) {
                finished = true;
            } else {
                currentFrame = 0;
            }
        }

        try {
            onFrame.apply(currentFrame, this, object);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @SneakyThrows
    public Animation<T> clone() {
        Animation<T> clone = (Animation<T>) super.clone();
        clone.finished = false;
        clone.currentFrame = 0;
        clone.clock = null;
        return clone;
    }

    public Animation<T> interval(long time, TimeUnit unit) {
        return interval(unit.toMillis(time));
    }
}
