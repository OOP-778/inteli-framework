package com.oop.inteliframework.entity.hologram.animated;

import com.oop.inteliframework.animation.AnimatedText;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AnimationCache {

  private String lastRequested;
  private AnimatedText animatedText;
}
