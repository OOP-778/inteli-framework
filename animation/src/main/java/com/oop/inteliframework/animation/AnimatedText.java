package com.oop.inteliframework.animation;

import com.oop.inteliframework.commons.util.InteliPair;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@RequiredArgsConstructor
@ToString
public class AnimatedText {
  private final String indexedText;
  private final List<ContentAnimation> animations;
  private String lastFrame = "";

  public InteliPair<String, Boolean> update() {
    String cleanLine = indexedText;
    for (int i = 0; i < animations.size(); i++) {
      ContentAnimation contentAnimation = animations.get(i);

      if (System.currentTimeMillis() >= contentAnimation.runAt()) {
        if (contentAnimation.runAt() != -1) contentAnimation.next();
        contentAnimation.runAt(System.currentTimeMillis() + contentAnimation.interval());
      }

      cleanLine = cleanLine.replace("$" + i, contentAnimation.currentFrame());
    }

    InteliPair<String, Boolean> result =
        new InteliPair<>(cleanLine, !cleanLine.contentEquals(lastFrame));
    lastFrame = cleanLine;
    return result;
  }
}
