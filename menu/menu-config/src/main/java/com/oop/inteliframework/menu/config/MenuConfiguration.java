package com.oop.inteliframework.menu.config;

import com.google.common.base.Preconditions;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.commons.util.StringFormat;
import com.oop.inteliframework.config.api.configuration.PlainConfig;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.ParentNode;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import com.oop.inteliframework.item.type.item.InteliItem;
import com.oop.inteliframework.item.type.item.InteliItemMeta;
import com.oop.inteliframework.menu.button.IButton;
import com.oop.inteliframework.menu.button.state.StateComponent;
import com.oop.inteliframework.menu.config.modifiers.MenuModifier;
import com.oop.inteliframework.menu.data.DataComponent;
import lombok.Getter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class MenuConfiguration {

  private final Set<InteliPair<Predicate<ConfigButton>, Consumer<ConfigButton>>> buttonModifiers =
      new HashSet<>();
  private final List<String> layout;
  private final String title;
  private final MenuLoader loader;
  private final List<ConfigButton> buttons = new ArrayList<>();

  public MenuConfiguration(PlainConfig menuConfig, MenuLoader loader) {
    try {
      this.loader = loader;

      // Load & Validate layout
      layout =
          menuConfig.get("layout", "Layout not found!").asValue().getAsListOf(String.class).stream()
              .map(in -> in.replaceAll("\\s+", ""))
              .collect(Collectors.toList());
      validateLayout();

      title = menuConfig.get("title", "Title not found!").asValue().getAs(String.class);

      // Initialize modifiers
      for (Map.Entry<String, Node> section : menuConfig.map(NodeIterator.PARENT).entrySet()) {
        MenuModifier handler = loader.getModifier(section.getKey());
        if (handler != null) {
          handler.handle(section.getValue().asParent(), this);
        }
      }

      // Initialize Buttons
      ParentNode buttons = menuConfig.get("buttons", "Failed to find buttons section").asParent();

      for (Map.Entry<String, Node> buttonSection : buttons.map(NodeIterator.PARENT).entrySet()) {
        try {
          loadButton(buttonSection.getKey(), buttonSection.getValue().asParent());
        } catch (Throwable throwable) {
          throw new IllegalStateException(
              StringFormat.format("Failed to load button by {}", buttonSection.getKey()),
              throwable);
        }
      }

    } catch (Throwable throwable) {
      throw new IllegalStateException(
          "Failed to load menu by name: " + menuConfig.file().getName() + " cause ", throwable);
    }
  }

  private void loadButton(String key, ParentNode buttonSection) {
    // Check if button has multiple states
    if (!buttonSection.isPresent("material")
        && buttonSection.list(NodeIterator.PARENT).size() == 0) {
      throw new IllegalStateException("Material nor states were not found!");
    }

    ConfigButton configButton = new ConfigButton();
    configButton.setLetter(key);
    IButton button = new IButton();
    configButton.setButton(button);

    buttons.add(configButton);

    Runnable afterBuild =
        () -> {
          button.applyComponent(
              DataComponent.class,
              dc -> {
                if (configButton.getIdentifier() != null) {
                  dc.add(ConfigDataKeys.BUTTON_IDENTIFIER.name(), configButton.getIdentifier());
                }

                dc.add(ConfigDataKeys.BUTTON_LETTER.name(), configButton.getLetter());
              });

          for (InteliPair<Predicate<ConfigButton>, Consumer<ConfigButton>> buttonModifier :
              buttonModifiers) {
            if (!buttonModifier.getKey().test(configButton)) continue;
            buttonModifier.getValue().accept(configButton);
          }
        };

    // We have no states
    if (buttonSection.isPresent("material")) {
      AbstractInteliItem itemBuilder = loadState(buttonSection);
      button.setCurrentItem(itemBuilder::asBukkitStack);

      // Set identifier of the button if present
      buttonSection.ifPresent(
          "identifier",
          identifierNode ->
              configButton.setIdentifier(identifierNode.asValue().getAs(String.class)));

      afterBuild.run();
      return;
    }

    Consumer<ParentNode> statesLoader =
        (statesSection) -> {
          for (Map.Entry<String, Node> stateSection :
              statesSection.map(NodeIterator.PARENT).entrySet()) {
            button.applyComponent(
                StateComponent.class,
                states ->
                    states.addState(
                        stateSection.getKey(), loadState(stateSection.getValue().asParent())));
          }
          afterBuild.run();
        };

    buttonSection
        .findAt("identifier")
        .ifPresent(
            node -> {
              configButton.setIdentifier(node.asValue().getAs(String.class));

              ParentNode statesSection =
                  (ParentNode)
                      buttonSection
                          .findAt("states")
                          .filter(Node::isParent)
                          .orElseThrow(
                              () -> new IllegalStateException("Failed to find states section"));

              statesLoader.accept(statesSection);
            })
        .elseNot(
            () -> {
              statesLoader.accept(buttonSection);
            });
  }

  private AbstractInteliItem loadState(ParentNode stateSection) {
    AbstractInteliItem<InteliItemMeta, ?> itemBuilder =
        new InteliItem(
            InteliMaterial.matchMaterial(
                    stateSection
                        .get("material", "Material must be present!")
                        .asValue()
                        .getAs(String.class))
                .parseItem());

    stateSection.ifPresent(
        "lore",
        loreNode ->
            itemBuilder.applyMeta(
                meta ->
                    meta.applyLore(
                        lore -> lore.lore(loreNode.asValue().getAsListOf(String.class)))));

    stateSection.ifPresent(
        "display-name",
        displayNameNode ->
            itemBuilder.applyMeta(
                meta -> meta.name(displayNameNode.asValue().getAs(String.class))));

    return itemBuilder;
  }

  private void validateLayout() {
    for (String s : layout) {
      Preconditions.checkArgument(
          s.length() == 9, "Invalid Layout Row Bounds (" + s.length() + "/" + 9 + ")");
    }
  }

  public Optional<ConfigButton> findButton(Predicate<ConfigButton> predicate) {
    return buttons.stream().filter(predicate).findFirst();
  }

  public void addModifier(Predicate<ConfigButton> predicate, Consumer<ConfigButton> consumer) {
    buttonModifiers.add(new InteliPair<>(predicate, consumer));
  }
}
