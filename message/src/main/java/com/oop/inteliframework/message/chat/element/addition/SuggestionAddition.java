package com.oop.inteliframework.message.chat.element.addition;

import com.oop.inteliframework.message.Replacer;
import lombok.*;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.function.Consumer;

@Accessors(chain = true, fluent = true)
@ToString
@RequiredArgsConstructor
@Setter
public class SuggestionAddition implements Addition<SuggestionAddition> {
  @NonNull @Getter private String suggestion;

  protected SuggestionAddition() {}

  @Override
  public SuggestionAddition clone() {
    return new SuggestionAddition(suggestion);
  }

  @Override
  public TextComponent apply(TextComponent textComponent) {
    return textComponent.clickEvent(ClickEvent.suggestCommand(suggestion));
  }

  @Override
  public SuggestionAddition replace(Replacer replacer) {
    this.suggestion = replacer.accept(suggestion);
    return this;
  }
}
