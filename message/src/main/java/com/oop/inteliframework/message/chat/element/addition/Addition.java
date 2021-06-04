package com.oop.inteliframework.message.chat.element.addition;

import com.oop.inteliframework.message.api.Replaceable;
import net.kyori.adventure.text.TextComponent;

public interface Addition<T extends Addition> extends Cloneable, Replaceable<T> {
  T clone();

  TextComponent apply(TextComponent textComponent);
}
