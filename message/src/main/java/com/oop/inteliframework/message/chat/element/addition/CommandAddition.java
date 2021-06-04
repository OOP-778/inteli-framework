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
public class CommandAddition implements Addition<CommandAddition> {
  @NonNull @Getter private String command;

  protected CommandAddition() {}

  @Override
  public CommandAddition clone() {
    return new CommandAddition(command);
  }

  @Override
  public TextComponent apply(TextComponent textComponent) {
    return textComponent.clickEvent(ClickEvent.runCommand(command));
  }

  @Override
  public CommandAddition replace(Replacer replacer) {
    this.command = replacer.accept(command);
    return this;
  }
}
