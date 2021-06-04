package com.oop.inteliframework.message.chat.element;

import com.oop.inteliframework.message.ComponentUtil;
import com.oop.inteliframework.message.Replacer;
import com.oop.inteliframework.message.api.Replaceable;
import com.oop.inteliframework.message.api.Sendable;
import com.oop.inteliframework.message.chat.element.addition.*;
import lombok.Getter;
import lombok.ToString;
import net.kyori.adventure.audience.Audience;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Getter
@ToString
public class LineContentElement implements Additionable<LineContentElement>, Sendable, Cloneable, Replaceable<LineContentElement> {
  private String text;
  private final Map<Class, Addition> additions = new HashMap<>();

  public LineContentElement(String text) {
    this.text = text;
  }

  protected <A extends Addition> A getOrCreateAddition(Class<A> additionClass) {
    return (A)
        additions.computeIfAbsent(
            additionClass,
            $ -> {
              try {
                Constructor<A> constructor = additionClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
              } catch (Throwable t) {
                throw new IllegalStateException("Empty constructor not found for " + additionClass);
              }
            });
  }

  @Override
  public LineContentElement hoverText(Consumer<HoverTextAddition> hoverTextConsumer) {
    hoverTextConsumer.accept(getOrCreateAddition(HoverTextAddition.class));
    return this;
  }

  @Override
  public LineContentElement command(Consumer<CommandAddition> commandAdditionConsumer) {
    commandAdditionConsumer.accept(getOrCreateAddition(CommandAddition.class));
    return this;
  }

  @Override
  public LineContentElement hoverItem(Consumer<HoverItemAddition> hoverItemAdditionConsumer) {
    hoverItemAdditionConsumer.accept(getOrCreateAddition(HoverItemAddition.class));
    return this;
  }

  @Override
  public LineContentElement suggestion(Consumer<SuggestionAddition> suggestionAdditionConsumer) {
    suggestionAdditionConsumer.accept(getOrCreateAddition(SuggestionAddition.class));
    return this;
  }

  @Override
  public LineContentElement chat(Consumer<ChatAddition> chatAdditionConsumer) {
    chatAdditionConsumer.accept(getOrCreateAddition(ChatAddition.class));
    return this;
  }

  @Override
  public Set<Addition> additionList() {
    return new HashSet<>(additions.values());
  }

  @Override
  public void send(Audience receiver) {
    receiver.sendMessage(ComponentUtil.colorizeFromBukkit(text));
  }

  @Override
  public LineContentElement clone() {
    return new LineContentElement(text);
  }

  @Override
  public LineContentElement replace(Replacer replacer) {
    this.text = replacer.accept(text);
    return this;
  }
}
