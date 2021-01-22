package com.oop.inteliframework.hologram.animation;

import com.oop.inteliframework.commons.util.InteliPair;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AnimationHandler {
    private final String indexedText;
    private final List<ContentAnimation> animations;

    private String lastText;

    public InteliPair<String, Boolean> update() {
        String cleanLine = indexedText;
        for (int i = 0; i < animations.size(); i++) {
            ContentAnimation contentAnimation = animations.get(i);

            if (contentAnimation.runAt() != -1 && contentAnimation.runAt() >= System.currentTimeMillis())
                contentAnimation.next();

            cleanLine = cleanLine.replace("$" + i, contentAnimation.currentFrame());
            contentAnimation.runAt(System.currentTimeMillis() + contentAnimation.interval());
        }

        InteliPair<String, Boolean> result = new InteliPair<>(cleanLine, !cleanLine.contentEquals(lastText));
        lastText = cleanLine;
        return result;
    }
}
