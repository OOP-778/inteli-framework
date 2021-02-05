package com.oop.intelimenus.config;

import com.google.common.base.Preconditions;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.config.configuration.PlainConfig;
import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.NodeIteratorType;
import com.oop.inteliframework.config.node.ParentNode;
import com.oop.intelimenus.button.IButton;
import com.oop.intelimenus.button.state.StateComponent;
import com.oop.intelimenus.config.modifiers.MenuModifier;
import com.oop.intelimenus.data.DataComponent;
import com.oop.intelimenus.interfaces.MenuItemBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class MenuConfiguration {

    private final Set<InteliPair<Predicate<ConfigButton>, Consumer<ConfigButton>>> buttonModifiers = new HashSet<>();
    private final List<String> layout;
    private final String title;
    private final MenuLoader loader;
    private final List<ConfigButton> buttons = new ArrayList<>();

    public MenuConfiguration(PlainConfig menuConfig, MenuLoader loader) {
        try {
            this.loader = loader;

            // Load & Validate layout
            layout = menuConfig.getAsValueOrThrow("layout", "Layout not found!")
                .getAsListOf(String.class)
                .stream()
                .map(in -> in.replaceAll("\\s+", ""))
                .collect(Collectors.toList());
            validateLayout();

            title = menuConfig.getAsValueOrThrow("title", "Title not found!").getAs(String.class);

            // Initialize modifiers
            for (Node section : menuConfig.list(NodeIteratorType.PARENTABLE)) {
                MenuModifier handler = loader.getModifier(section.key());
                if (handler != null) {
                    handler.handle(section.asParentSafe(), this);
                }
            }

            // Initialize Buttons
            ParentNode buttons = (ParentNode) menuConfig.findAt("buttons")
                .filter(Node::isParentable)
                .orElseThrow(() -> new IllegalStateException("Failed to find buttons section"));
            for (Node buttonSection : buttons.list(NodeIteratorType.PARENTABLE)) {
                loadButton(buttonSection.asParentSafe());
            }

        } catch (Throwable throwable) {
            throw new IllegalStateException(
                "Failed to load menu by name: " + menuConfig.file().getName() + " cause ",
                throwable);
        }
    }

    private void throwButtonLoadError(ParentNode section, String error) {
        throw new IllegalStateException(
            "Failed to load a button at: " + section.path() + " cause: " + error);
    }

    private void loadButton(ParentNode buttonSection) {
        // Check if button has multiple states
        if (!buttonSection.isPresent("material") && buttonSection.list(NodeIteratorType.PARENTABLE).size() == 0) {
            throwButtonLoadError(buttonSection, "Material nor states were not found!");
        }

        ConfigButton configButton = new ConfigButton();
        configButton.setLetter(buttonSection.key());
        IButton button = new IButton();
        configButton.setButton(button);

        buttons.add(configButton);

        Runnable afterBuild = () -> {
            button.applyComponent(DataComponent.class, dc -> {
                if (configButton.getIdentifier() != null) {
                    dc.add(ConfigDataKeys.BUTTON_IDENTIFIER.name(), configButton.getIdentifier());
                }

                dc.add(ConfigDataKeys.BUTTON_LETTER.name(), configButton.getLetter());
            });

            for (InteliPair<Predicate<ConfigButton>, Consumer<ConfigButton>> buttonModifier : buttonModifiers) {
                if (!buttonModifier.getKey().test(configButton)) continue;
                buttonModifier.getValue().accept(configButton);
            }
        };

        // We have no states
        if (buttonSection.isPresent("material")) {
            MenuItemBuilder itemBuilder = loadState(buttonSection);
            button.setCurrentItem(itemBuilder::getItem);

            // Set identifier of the button if present
            buttonSection
                .findAt("identifier")
                .map(node -> node.asValueSafe().getAs(String.class))
                .ifPresent(configButton::setIdentifier);

            afterBuild.run();
            return;
        }

        Consumer<ParentNode> statesLoader = (statesSection) -> {
            for (Node stateSection : statesSection.list(NodeIteratorType.PARENTABLE)) {
                button
                    .applyComponent(StateComponent.class,
                        states -> states.addState(stateSection.key(), loadState(stateSection.asParentSafe())));
            }
            afterBuild.run();
        };

        buttonSection
            .findAt("identifier")
            .ifPresent(node -> {
                configButton.setIdentifier(node.asValueSafe().getAs(String.class));

                ParentNode statesSection = (ParentNode) buttonSection
                    .findAt("states")
                    .filter(Node::isParentable)
                    .orElseThrow(() -> new IllegalStateException("Failed to find states section"));

                statesLoader.accept(statesSection);
            })
            .elseNot(() -> {
                statesLoader.accept(buttonSection);
            });
    }

    private MenuItemBuilder loadState(ParentNode stateSection) {
        MenuItemBuilder itemBuilder = MenuItemBuilder
            .of(loader.getItemProvider().apply(stateSection.getAsValueOrThrow("material", "Material must be present!").getAs(String.class)));

        stateSection.findAt("lore")
            .map(node -> node.asValueSafe().getAsListOf(String.class))
            .ifPresent(itemBuilder::lore);

        stateSection.findAt("display name")
            .map(node -> node.asValueSafe().getAs(String.class))
            .ifPresent(itemBuilder::displayName);

        return itemBuilder;
    }

    private void validateLayout() {
        for (String s : layout) {
            Preconditions.checkArgument(s.length() == 9,
                "Invalid Layout Row Bounds (" + s.length() + "/" + 9 + ")");
        }
    }

    public Optional<ConfigButton> findButton(Predicate<ConfigButton> predicate) {
        return buttons.stream().filter(predicate).findFirst();
    }

    public void addModifier(Predicate<ConfigButton> predicate, Consumer<ConfigButton> consumer) {
        buttonModifiers.add(new InteliPair<>(predicate, consumer));
    }
}
