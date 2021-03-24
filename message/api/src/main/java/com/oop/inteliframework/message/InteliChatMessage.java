package com.oop.inteliframework.message;

import com.oop.inteliframework.api.InteliMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class InteliChatMessage implements InteliMessage<InteliChatMessage> {
  private final List<Component> componentList;

  public InteliChatMessage(List<Component> componentList) {
    this.componentList = componentList;
  }

  @Override
  public InteliChatMessage replace(Consumer<TextReplacementConfig.Builder> builderConsumer) {
    List<Component> newComponentList = new LinkedList<>();
    for (Component component : componentList) {
      newComponentList.add(component.replaceText(builderConsumer));
    }
    return new InteliChatMessage(newComponentList);
  }

  @Override
  public Component toComponent() {
    return null;
  }

  @Override
  public void send(Audience audience) {}
}
