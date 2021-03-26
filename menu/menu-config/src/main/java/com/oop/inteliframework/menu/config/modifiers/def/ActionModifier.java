package com.oop.inteliframework.menu.config.modifiers.def;

import com.google.common.base.Preconditions;
import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.ParentNode;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import com.oop.inteliframework.menu.button.builder.TriggerBuilder;
import com.oop.inteliframework.menu.config.MenuConfiguration;
import com.oop.inteliframework.menu.config.modifiers.MenuModifier;
import com.oop.inteliframework.menu.config.ConfigButton;
import com.oop.inteliframework.menu.trigger.types.ButtonClickTrigger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ActionModifier extends MenuModifier {
  private final Map<String, BiConsumer<ConfigButton, BaseParentNode>> actionTypes = new HashMap<>();

  public ActionModifier() {
    registerAction(
        "command",
        (button, section) -> {
          // Check if all values are there
          Preconditions.checkArgument(section.isPresent("command"), "Command is not found");

          Function<ButtonClickTrigger, CommandSender> senderFunction =
              trigger -> {
                InteliOptional<String> sender =
                    section.findAt("sender").map(node -> node.asValue().getAs(String.class));
                if (!sender.isPresent()) {
                  return trigger.getPlayer();
                }

                return sender.get().equalsIgnoreCase("console")
                    ? Bukkit.getConsoleSender()
                    : trigger.getPlayer();
              };

          String command =
              section
                  .findAt("command")
                  .map(node -> node.asValue().getAs(String.class))
                  .orElse(null);

          System.out.println("one");

          TriggerBuilder.of(ButtonClickTrigger.class)
              .onTrigger(
                  trigger -> {
                    String replace = command.replace("%player%", trigger.getPlayer().getName());
                    Bukkit.dispatchCommand(senderFunction.apply(trigger), replace);

                    trigger.setCancelled(true);
                  })
              .apply(button.getButton());
        });

    registerAction(
        "internal",
        (button, section) -> {
          String doType =
              section
                  .get("do", "Failed to find internal action type")
                  .asValue()
                  .getAs(String.class);

          if (doType.equalsIgnoreCase("close")) {
            TriggerBuilder.of(ButtonClickTrigger.class)
                .onTrigger(
                    trigger -> {
                      trigger.setCancelled(true);
                      trigger.getMenu().closeAction(null);
                    })
                .apply(button.getButton());
            return;
          }

          if (doType.equalsIgnoreCase("refresh")) {
            TriggerBuilder.of(ButtonClickTrigger.class)
                .onTrigger(
                    trigger -> {
                      trigger.getMenu().refreshAction();
                      trigger.setCancelled(true);
                    })
                .apply(button.getButton());
          }
        });
  }

  @Override
  public String getIdentifier() {
    return "actions";
  }

  @Override
  public void handle(ParentNode section, MenuConfiguration configuration) {
    // Identifiers
    for (Map.Entry<String, Node> actionSection : section.map(NodeIterator.PARENT).entrySet()) {
      Predicate<ConfigButton> predicate;
      String key = actionSection.getKey();

      // We got a letter
      if (key.toCharArray().length == 1) {
        predicate = button -> button.getLetter().equalsIgnoreCase(key);

      } else {
        predicate =
            button ->
                button.getIdentifier() != null && button.getIdentifier().equalsIgnoreCase(key);
      }

      // If this contains single action
      List<Node> list = actionSection.getValue().asParent().list(NodeIterator.PARENT);
      if (list.size() == 0) {
        load(predicate, (BaseParentNode) actionSection, configuration);

      } else {
        for (Node value : list) {
          load(predicate, (BaseParentNode) value, configuration);
        }
      }
    }
  }

  private void load(
      Predicate<ConfigButton> predicate,
      BaseParentNode actionSection,
      MenuConfiguration configuration) {
    Preconditions.checkArgument(
        actionSection.isPresent("type"), "The action section doesn't have a type!");
    String type =
        actionSection
            .findAt("type")
            .map(node -> node.asValue().asValue().getAs(String.class))
            .orElse(null);

    BiConsumer<ConfigButton, BaseParentNode> actionHandler = actionTypes.get(type.toLowerCase());
    Preconditions.checkArgument(
        actionHandler != null, "Failed to find action by type: " + type.toLowerCase());

    configuration.addModifier(
        predicate,
        configButton -> {
          actionHandler.accept(configButton, actionSection);
        });
  }

  public void registerAction(String type, BiConsumer<ConfigButton, BaseParentNode> consumer) {
    actionTypes.put(type.toLowerCase(), consumer);
  }
}
