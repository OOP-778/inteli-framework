package com.oop.inteliframework.message.chat.element.addition;

import com.oop.inteliframework.message.ComponentUtil;
import com.oop.inteliframework.message.Replacer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class HoverTextAddition implements Addition<HoverTextAddition> {
  @Getter private List<String> hoverText = new LinkedList<>();

  protected HoverTextAddition() {}

  public HoverTextAddition add(@NonNull String... text) {
    hoverText.addAll(Arrays.asList(text));
    return this;
  }

  public HoverTextAddition set(@NonNull List<String> hoverText) {
    this.hoverText.clear();
    this.hoverText.addAll(hoverText);
    return this;
  }

  public HoverTextAddition set(@NonNull String... text) {
    return set(Arrays.asList(text));
  }

  public HoverTextAddition clear() {
    hoverText.clear();
    return this;
  }

  @Override
  @SneakyThrows
  public HoverTextAddition clone() {
    return new HoverTextAddition(new ArrayList<>(hoverText));
  }

  @Override
  public TextComponent apply(TextComponent textComponent) {
    return textComponent.hoverEvent(
        HoverEvent.showText(ComponentUtil.listedTextOf(hoverText.toArray(new String[0]))));
  }


  @Override
  public HoverTextAddition replace(Replacer replacer) {
    this.hoverText = replacer.accept(hoverText);
    return this;
  }
}
