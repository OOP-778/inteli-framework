package com.oop.inteliframework.animation.provider;

import com.oop.inteliframework.animation.ContentAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EraseAnimation extends AbstractProvider {
  @Override
  public ContentAnimation create(String text, Map<String, Object> options) {
    ContentAnimation animation = super.create(text, options);

    boolean fade = options.containsKey("fade");
    boolean reverse = options.containsKey("reverse");
    List<String> frames = new ArrayList<>();
    animation.frames(frames);

    if (!fade) {
      for (int i = 0; i < text.length(); ++i) {
        frames.add(text.substring(0, text.length() - i));
      }
    } else {
      for (int i = 0; i < text.length() + 3; ++i) {
        int startFade = text.length() - i;
        String whiteText = text.substring(0, Math.min(Math.max(startFade, 0), text.length()));
        String lightGrayText =
            text.substring(
                Math.max(0, Math.min(startFade, text.length())),
                Math.min(text.length(), Math.max(0, startFade + 1)));
        String darkGrayText =
            text.substring(
                Math.max(0, Math.min(startFade + 1, text.length())),
                Math.min(text.length(), Math.max(0, startFade + 2)));
        String blackText =
            text.substring(
                Math.max(0, Math.min(startFade + 2, text.length())),
                Math.min(text.length(), Math.max(0, startFade + 3)));
        frames.add(whiteText + "§7" + lightGrayText + "§8" + darkGrayText + "§0" + blackText);
      }
    }

    frames.add("");

    if (reverse) {
      for (int i = frames.size() - 2; i >= 0; i--) {
        frames.add(frames.get(i));
      }
    }

    return animation;
  }
}
