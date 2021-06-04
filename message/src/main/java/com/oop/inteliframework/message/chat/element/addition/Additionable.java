package com.oop.inteliframework.message.chat.element.addition;

import java.util.Set;
import java.util.function.Consumer;

public interface Additionable<T> {
  T hoverText(Consumer<HoverTextAddition> hoverTextConsumer);

  T command(Consumer<CommandAddition> commandAdditionConsumer);

  T hoverItem(Consumer<HoverItemAddition> hoverItemAdditionConsumer);

  T suggestion(Consumer<SuggestionAddition> suggestionAdditionConsumer);

  T chat(Consumer<ChatAddition> chatAdditionConsumer);

  Set<Addition> additionList();
}
