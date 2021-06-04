package com.oop.inteliframework.message.config;

import com.google.common.primitives.Ints;
import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.ParentNode;
import com.oop.inteliframework.config.node.api.ValueNode;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import com.oop.inteliframework.message.ComponentUtil;
import com.oop.inteliframework.message.actionbar.InteliActionBarMessage;
import com.oop.inteliframework.message.api.InteliMessage;
import com.oop.inteliframework.message.bossbar.BossBarProps;
import com.oop.inteliframework.message.bossbar.InteliBossBarMessage;
import com.oop.inteliframework.message.chat.InteliChatMessage;
import com.oop.inteliframework.message.chat.element.ChatLineElement;
import com.oop.inteliframework.message.chat.element.LineContentElement;
import com.oop.inteliframework.message.chat.element.addition.*;
import com.oop.inteliframework.message.title.InteliTitleMessage;
import com.oop.inteliframework.message.title.TitleProps;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YamlMessage {
  public static void save(InteliMessage message, String path, ParentNode node) {
    if (message instanceof InteliChatMessage) {
      Chat.save((InteliChatMessage) message, path, node);
      return;
    }

    if (message instanceof InteliTitleMessage) {
      saveTitle((InteliTitleMessage) message, path, node);
      return;
    }

    if (message instanceof InteliActionBarMessage) {
      saveActionBar((InteliActionBarMessage) message, path, node);
      return;
    }

    if (message instanceof InteliBossBarMessage) {
      saveBossBar((InteliBossBarMessage) message, path, node);
      return;
    }
  }

  public static InteliMessage load(ParentNode section) {
    InteliOptional<Node> optType = section.findAt("type");
    if (optType.isPresent()) {
      String type = optType.get().asValue().getAs(String.class);
      if (type.equalsIgnoreCase("chat")) return Chat.load(section);
      else if (type.equalsIgnoreCase("actionbar")) return loadActionBar(section);
      else if (type.equalsIgnoreCase("title")) return loadTitle(section);
      else if (type.equalsIgnoreCase("bossbar")) return loadBossBar(section);
      else return Chat.load(section);
    }

    return Chat.load(section);
  }

  public static InteliMessage load(String path, ParentNode parentNode) {
    InteliOptional<Node> optionalNode = parentNode.findAt(path);
    if (optionalNode.isPresent()) {
      if (optionalNode.get().isParent()) return load(optionalNode.get().asParent());

      return Chat.load(optionalNode.get().asValue());
    }
    return null;
  }

  private static InteliTitleMessage loadTitle(ParentNode parentNode) {
    final TitleProps.TitlePropsBuilder propsBuilder = TitleProps.builder();

    parentNode
        .findAt("props")
        .map(Node::asParent)
        .ifPresent(
            propsNode -> {
              propsNode.ifPresent(
                  "stay", node -> propsBuilder.stay(node.asValue().getAs(int.class)));
              propsNode.ifPresent(
                  "fade-in", node -> propsBuilder.fadeIn(node.asValue().getAs(int.class)));
              propsNode.ifPresent(
                  "fade-out", node -> propsBuilder.fadeOut(node.asValue().getAs(int.class)));
            });

    Component title, subtitle;
    title =
        ComponentUtil.colorizeFromBukkit(
            parentNode.get("title", "Title not found in section").asValue().getAs(String.class));
    subtitle =
        parentNode
            .findAt("sub-title")
            .map(Node::asValue)
            .map(node -> node.getAs(String.class))
            .map(ComponentUtil::colorizeFromBukkit)
            .orElse(null);
    return new InteliTitleMessage(propsBuilder.build(), title, subtitle);
  }

  private static InteliActionBarMessage loadActionBar(ParentNode section) {
    InteliActionBarMessage actionBarMessage = new InteliActionBarMessage(null);
    actionBarMessage.content(
        ComponentUtil.colorizeFromBukkit(
            section
                .get("content", "Content must be found in action bar type message!")
                .asValue()
                .getAs(String.class)));
    return actionBarMessage;
  }

  public static void saveBossBar(InteliBossBarMessage message, String path, ParentNode parentNode) {
    final ParentNode node = parentNode.getOrAssign(path, BaseParentNode::new).asParent();
    final ParentNode propsNode = node.getOrAssign("props", BaseParentNode::new).asParent();

    node.set("type", "bossbar");
    node.set("text", ComponentUtil.contentFromComponent(message.getText()));

    propsNode.set("color", message.getProps().color().name());
    propsNode.set("overlay", message.getProps().overlay().name());
    propsNode.set("percentage", message.getProps().percentage());
    propsNode.set("stay", message.getProps().stay());
  }

  public static InteliBossBarMessage loadBossBar(ParentNode parentNode) {
    final BossBarProps.BossBarPropsBuilder propsBuilder = BossBarProps.builder();
    parentNode
        .findAt("props")
        .map(Node::asParent)
        .ifPresent(
            propsNode -> {
              propsNode.ifPresent(
                  "stay", node -> propsBuilder.stay(node.asValue().getAs(int.class)));
              propsNode.ifPresent(
                  "color",
                  node ->
                      propsBuilder.color(
                          BossBar.Color.valueOf(node.asValue().getAs(String.class).toUpperCase())));
              propsNode.ifPresent(
                  "percentage", node -> propsBuilder.percentage(node.asValue().getAs(float.class)));
              propsNode.ifPresent(
                  "overlay",
                  node ->
                      propsBuilder.overlay(
                          BossBar.Overlay.valueOf(
                              node.asValue().getAs(String.class).toUpperCase())));
            });

    return new InteliBossBarMessage(
        propsBuilder.build(),
        ComponentUtil.colorizeFromBukkit(
            parentNode.get("text", "Text is required for boss bar").asValue().getAs(String.class)));
  }

  public static void saveActionBar(
      InteliActionBarMessage message, String path, ParentNode parentNode) {
    final ParentNode saveNode = parentNode.getOrAssign(path, BaseParentNode::new).asParent();

    saveNode.set("type", "actionbar");
    saveNode.set("content", ComponentUtil.contentFromComponent(message.content()));
  }

  public static void saveTitle(InteliTitleMessage message, String path, ParentNode parentNode) {
    final ParentNode node = parentNode.getOrAssign(path, BaseParentNode::new).asParent();
    final ParentNode propsNode = node.getOrAssign("props", BaseParentNode::new).asParent();

    node.set("type", "title");
    node.set("title", ComponentUtil.contentFromComponent(message.getTitle()));

    propsNode.set("fade-in", message.getProps().fadeIn());
    propsNode.set("stay", message.getProps().stay());
    propsNode.set("fade-out", message.getProps().fadeOut());

    if (message.getSubtitle() != null) {
      node.set("sub-title", ComponentUtil.contentFromComponent(message.getSubtitle()));
    }
  }

  public static class Chat {
    public static void save(InteliChatMessage message, String path, ParentNode valuable) {
      /*
      If message doesn't have any attributes like center and it's one lined.
      */
      if (!requiresSection(message)) {
        if (requiresSection(message.element())) {
          ChatLineElement line = message.element();
          ParentNode parentNode = valuable.getOrAssign(path, BaseParentNode::new).asParent();
          parentNode.set("type", "chat");

          save(line, parentNode);
          return;
        }
        valuable.set(path, message.element().element().getText());
        return;
      }

      if (!message.isCentered() && allOneLined(message)) {
        valuable.set(path, message.stream().map(ChatLineElement::raw).collect(Collectors.toList()));
        return;
      }

      ParentNode messageNode = valuable.getOrAssign(path, BaseParentNode::new).asParent();
      messageNode.set("type", "chat");
      if (message.isCentered()) messageNode.set("center", true);

      if (message.size() == 1) {
        ChatLineElement line = message.element();
        save(line, messageNode);
        return;
      }

      ParentNode linesNode = messageNode.getOrAssign("lines", BaseParentNode::new).asParent();
      int i = 1;
      for (ChatLineElement line : message) {

        if (requiresSection(line)) {
          ParentNode lineNode = linesNode.getOrAssign(i + "", BaseParentNode::new).asParent();
          save(line, lineNode);
          i++;
          continue;
        }

        linesNode.set(i + "", line.element().getText());
        i++;
      }
    }

    private static void save(ChatLineElement line, ParentNode lineNode) {
      if (line.centered()) lineNode.set("center", true);

      if (line.size() == 1) {
        LineContentElement content = line.element();
        save(content, lineNode);
        return;
      }

      if (line.size() > 1) {
        ParentNode allContentNode = lineNode.getOrAssign("content", BaseParentNode::new).asParent();

        int i = 1;
        for (LineContentElement content : line) {
          if (!requiresSection(content)) {
            allContentNode.set(i + "", content.getText());
            i++;
            continue;
          }

          ParentNode contentNode =
              allContentNode.getOrAssign(i + "", BaseParentNode::new).asParent();
          save(content, contentNode);
          i++;
        }
      }
    }

    private static void save(LineContentElement content, ParentNode contentNode) {
      contentNode.set("text", content.getText());

      for (Addition addition : content.additionList()) {
        if (addition instanceof CommandAddition) {
          contentNode.set("command", ((CommandAddition) addition).command());
          continue;
        }

        if (addition instanceof HoverTextAddition) {
          contentNode.set("hover", ((HoverTextAddition) addition).getHoverText());
          continue;
        }

        if (addition instanceof SuggestionAddition) {
          contentNode.set("suggestion", ((SuggestionAddition) addition).suggestion());
          continue;
        }

        if (addition instanceof ChatAddition) {
          contentNode.set("chat", ((ChatAddition) addition).message());
        }
      }
    }

    private static boolean requiresSection(InteliChatMessage message) {
      if (isMultiLine(message) || message.isCentered()) return true;
      return requiresSection(message.element());
    }

    private static boolean requiresSection(ChatLineElement line) {
      if (line.size() > 1) return true;

      LineContentElement content = line.element();
      return requiresSection(content);
    }

    private static boolean requiresSection(LineContentElement content) {
      return !content.additionList().isEmpty();
    }

    private static boolean isMultiLine(InteliChatMessage message) {
      return message.size() > 1;
    }

    private static boolean allOneLined(InteliChatMessage message) {
      return message.stream().noneMatch(Chat::requiresSection);
    }

    public static InteliChatMessage load(ValueNode node) {
      InteliChatMessage message = new InteliChatMessage();
      if (node.value() instanceof List) {
        for (String s : node.getAsListOf(String.class)) {
          message.add(new ChatLineElement(s));
        }
        return message;
      }

      message.add(new ChatLineElement(node.getAs(String.class)));
      return message;
    }

    public static InteliChatMessage load(ParentNode parentNode) {
      InteliChatMessage message = new InteliChatMessage();
      parentNode.ifPresent(
          "center", centerNode -> message.setCentered(centerNode.asValue().getAs(boolean.class)));

      if (parentNode.isPresent("text")) {
        message.add(new ChatLineElement(Collections.singleton(loadContentLine(parentNode))));
        return message;
      }

      InteliOptional<ParentNode> optContent = parentNode.findAt("content").map(Node::asParent);
      InteliOptional<ParentNode> optLines = parentNode.findAt("lines").map(Node::asParent);
      if (optContent.isPresent()) return message.apply(m -> m.add(loadLine(optContent.get())));

      if (optLines.isPresent()) {
        ParentNode linesSection = optLines.get();
        Map<Integer, ChatLineElement> lineMap = new HashMap<>();
        linesSection
            .map(NodeIterator.ALL)
            .forEach(
                (key, node) -> {
                  Integer parsedKey = Ints.tryParse(key);
                  if (parsedKey == null) return;

                  if (node instanceof ParentNode) {
                    ParentNode contentNode =
                        ((ParentNode) node).get("content", "Missing content section").asParent();
                    lineMap.put(Integer.parseInt(key), loadLine(contentNode));
                  }

                  lineMap.put(
                      Integer.parseInt(key),
                      new ChatLineElement(
                          new LineContentElement(node.asValue().getAs(String.class))));
                });

        lineMap.keySet().stream()
            .sorted()
            .forEach(pos -> message.apply(m -> m.add(lineMap.get(pos))));
      }
      return message;
    }

    private static ChatLineElement loadLine(ParentNode lineNode) {
      ChatLineElement line = new ChatLineElement();

      Map<Integer, LineContentElement> contentMap = new HashMap<>();
      lineNode
          .map(NodeIterator.ALL)
          .forEach(
              (key, node) -> {
                Integer parsedKey = Ints.tryParse(key);
                if (parsedKey == null) return;

                if (node instanceof ParentNode) {
                  contentMap.put(parsedKey, loadContentLine(node.asParent()));
                  return;
                }

                contentMap.put(
                    parsedKey, new LineContentElement(node.asValue().getAs(String.class)));
              });

      contentMap.keySet().stream().sorted().forEach(pos -> line.append(contentMap.get(pos)));
      return line;
    }

    private static LineContentElement loadContentLine(ParentNode contentNode) {
      LineContentElement lineContent =
          new LineContentElement(
              contentNode.get("text", "Text is required!").asValue().getAs(String.class));

      contentNode.ifPresent(
          "suggestion",
          suggestionNode ->
              lineContent.suggestion(
                  add -> add.suggestion(suggestionNode.asValue().getAs(String.class))));
      contentNode.ifPresent(
          "command",
          commandNode ->
              lineContent.command(add -> add.command(commandNode.asValue().getAs(String.class))));
      contentNode.ifPresent(
          "chat",
          chatNode -> lineContent.chat(add -> add.message(chatNode.asValue().getAs(String.class))));

      contentNode.ifPresent(
          "hover",
          hoverNode -> {
            if (hoverNode.asValue().value() instanceof List) {
              lineContent.hoverText(
                  add ->
                      add.add(
                          hoverNode.asValue().getAsListOf(String.class).toArray(new String[0])));
              return;
            }

            lineContent.hoverText(add -> add.add(hoverNode.asValue().getAs(String.class)));
          });

      return lineContent;
    }
  }
}
