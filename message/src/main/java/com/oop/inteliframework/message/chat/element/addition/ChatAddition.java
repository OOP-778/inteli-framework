package com.oop.inteliframework.message.chat.element.addition;

import com.oop.inteliframework.message.Replacer;
import lombok.*;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;

@Accessors(chain = true, fluent = true)
@ToString
@RequiredArgsConstructor
@Setter
public class ChatAddition implements Addition<ChatAddition> {
  @NonNull @Getter private String message;

  protected ChatAddition() {}

  @Override
  public ChatAddition clone() {
    return new ChatAddition(message);
  }

  @Override
  public TextComponent apply(TextComponent textComponent) {
    return textComponent.clickEvent(ClickEvent.runCommand(message));
  }

  @Override
  public ChatAddition replace(Replacer replacer) {
    this.message = replacer.accept(message);
    return this;
  }
}
