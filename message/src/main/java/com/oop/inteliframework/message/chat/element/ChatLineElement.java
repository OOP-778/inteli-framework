package com.oop.inteliframework.message.chat.element;

import com.google.common.base.Preconditions;
import com.oop.inteliframework.commons.util.InsertableList;
import com.oop.inteliframework.message.Replacer;
import com.oop.inteliframework.message.api.Componentable;
import com.oop.inteliframework.message.api.Replaceable;
import com.oop.inteliframework.message.api.Sendable;
import com.oop.inteliframework.message.chat.InteliChatMessage;
import com.oop.inteliframework.message.chat.element.util.Centerer;
import com.oop.inteliframework.message.chat.element.util.ChatLineUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Accessors(chain = true, fluent = true)
@ToString
public class ChatLineElement extends InsertableList<LineContentElement>
    implements Cloneable, Sendable, Componentable, Replaceable<ChatLineElement> {
  @Getter @Setter private boolean centered = false;

  public ChatLineElement() {}

  public ChatLineElement(String content) {
    this(new LineContentElement(content));
  }

  public ChatLineElement(LineContentElement... elements) {
    this(Arrays.asList(elements));
  }

  public ChatLineElement(Collection<LineContentElement> elements) {
    addAll(elements);
  }

  public ChatLineElement replace(LineContentElement replace, LineContentElement to) {
    int i = indexOf(replace);
    Preconditions.checkArgument(i != -1, "Index cannot be -1");

    set(i, to);
    return this;
  }

  public ChatLineElement append(LineContentElement... content) {
    addAll(Arrays.asList(content));
    return this;
  }

  public ChatLineElement append(String content) {
    return append(new LineContentElement(content));
  }

  public ChatLineElement replace(String key, ChatLineElement... value) {
    Supplier<LineContentElement[]> cloneElements =
        () -> Arrays.stream(value).flatMap(Collection::stream).toArray(LineContentElement[]::new);

    for (LineContentElement lineContent : new ArrayList<>(this)) {
      if (!lineContent.getText().contains(key)) continue;

      String[] split = lineContent.getText().split(Pattern.quote(key));
      if (split.length == 2) {
        LineContentElement firstPart = new LineContentElement(split[0]);
        LineContentElement thirdPart = new LineContentElement(split[1]);

        replace(lineContent, firstPart);
        int index = insert(firstPart, InsertableList.InsertMethod.REPLACE, cloneElements.get());
        insert(index, thirdPart);
        continue;
      }

      if (split.length == 1) {
        LineContentElement firstPart = new LineContentElement(split[0]);
        replace(lineContent, firstPart);
        insert(firstPart, InsertableList.InsertMethod.AFTER, cloneElements.get());
        continue;
      }

      insert(lineContent, InsertableList.InsertMethod.REPLACE, cloneElements.get());
    }

    return this;
  }

  public ChatLineElement replace(String key, InteliChatMessage message) {
    return replace(key, message.toArray(new ChatLineElement[0]));
  }

  private int insert(int indexOf, ChatLineElement... lines) {
    for (ChatLineElement chatLineElement : lines) {
      for (LineContentElement lineContent : chatLineElement) {
        add(indexOf += 1, lineContent);
      }
    }
    return indexOf;
  }

  public ChatLineElement replace(String key, LineContentElement content) {
    return replace(key, new ChatLineElement(Collections.singleton(content)));
  }

  public TextComponent toComponent() {
    StringBuilder appendStart = new StringBuilder(), appendEnd = new StringBuilder();
    if (centered) {
      String content = raw();
      content = Centerer.getCenteredMessage(content);
      IntStream.range(1, ChatLineUtil.findSpaces(content, false))
          .forEach(i -> appendStart.append(" "));
      IntStream.range(1, ChatLineUtil.findSpaces(content, true))
          .forEach(i -> appendEnd.append(" "));
    }

    TextComponent component = ChatLineBuilder.buildComponents(this);
    TextComponent base = Component.text(appendStart.toString());
    component = component.append(Component.text(appendEnd.toString()));
    return base.append(component);
  }

  public void append(ChatLineElement parentLine) {
    addAll(parentLine);
  }

  @SneakyThrows
  public ChatLineElement clone() {
    ChatLineElement clone = new ChatLineElement();
    clone.centered = centered;
    clone.addAll(
        stream()
            .map(LineContentElement::clone)
            .collect(Collectors.toCollection(InsertableList::new)));
    return clone;
  }

  public String raw() {
    return stream()
        .map(LineContentElement::getText)
        .filter(s -> s.trim().length() > 0)
        .collect(Collectors.joining(""));
  }

  public LineContentElement findContent(Predicate<LineContentElement> filter) {
    return stream().filter(filter).findFirst().orElse(null);
  }

  public ChatLineElement removeContentIf(Predicate<LineContentElement> filter) {
    removeIf(filter);
    return this;
  }

  @Override
  public void send(Audience receiver) {
    receiver.sendMessage(toComponent());
  }

  @Override
  public ChatLineElement replace(Replacer replacer) {
    for (LineContentElement element : this) {
      element.replace(replacer);
    }
    return this;
  }
}
