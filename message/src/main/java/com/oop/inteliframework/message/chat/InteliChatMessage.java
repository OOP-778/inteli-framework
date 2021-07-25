package com.oop.inteliframework.message.chat;

import com.oop.inteliframework.commons.util.InsertableList;
import com.oop.inteliframework.message.Replacer;
import com.oop.inteliframework.message.api.InteliMessage;
import com.oop.inteliframework.message.chat.element.ChatLineElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@ToString
public class InteliChatMessage extends InsertableList<ChatLineElement> implements InteliMessage<InteliChatMessage> {
  @Getter @Setter private boolean centered = false;

  public InteliChatMessage(String... lines) {
    addAll(Arrays.stream(lines).map(ChatLineElement::new).collect(Collectors.toList()));
  }

  public InteliChatMessage(ChatLineElement... elements) {
    addAll(Arrays.asList(elements));
  }

  public InteliChatMessage(List<ChatLineElement> componentList) {
    addAll(componentList);
  }

  public InteliChatMessage() {}

  @Override
  public Component toComponent() {
    TextComponent component = Component.empty();
    Iterator<ChatLineElement> iterator = iterator();
    while (iterator.hasNext()) {
      component = component.append(iterator.next().toComponent());
      if (iterator.hasNext()) component = component.append(Component.newline());
    }

    return component;
  }

  @Override
  public void send(Audience audience) {
    audience.sendMessage(toComponent());
  }

  public InteliChatMessage apply(Consumer<InteliChatMessage> consumer) {
    consumer.accept(this);
    return this;
  }

  @Override
  public InteliChatMessage replace(Replacer replacer) {
    for (ChatLineElement element : this) {
      element.replace(replacer);
    }

    return this;
  }

  @Override
  public InteliChatMessage clone() {
    return new InteliChatMessage(
            stream()
            .map(ChatLineElement::clone)
            .collect(Collectors.toList())
    );
  }
}
