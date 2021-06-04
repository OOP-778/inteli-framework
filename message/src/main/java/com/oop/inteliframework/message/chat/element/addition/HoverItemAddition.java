package com.oop.inteliframework.message.chat.element.addition;

import com.oop.inteliframework.message.Replacer;
import com.oop.inteliframework.message.api.ItemDisplay;
import lombok.*;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.TextComponent;

import java.util.function.Consumer;

@Getter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
@ToString
public class HoverItemAddition implements Addition<HoverItemAddition> {
  @NonNull @Setter private ItemDisplay item;

  protected HoverItemAddition() {}

  @SneakyThrows
  @Override
  public HoverItemAddition clone() {
    return new HoverItemAddition(item.clone());
  }

  @Override
  public TextComponent apply(TextComponent textComponent) {
    return textComponent.hoverEvent(item.toHoverEvent());
  }

  @Override
  public HoverItemAddition replace(Replacer replacer) {
    item.replace(replacer);
    return this;
  }
}
