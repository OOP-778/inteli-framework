package com.oop.inteliframework.message;

import com.oop.inteliframework.message.api.InteliMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.Iterator;
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
    TextComponent component = Component.empty();
    Iterator<Component> iterator = componentList.iterator();
    while (iterator.hasNext()) {
      component = component.append(iterator.next());
      if (iterator.hasNext())
        component = component.append(Component.newline());
     }

    return component;
  }

  @Override
  public void send(Audience audience) {
    audience.sendMessage(toComponent());
  }
}
