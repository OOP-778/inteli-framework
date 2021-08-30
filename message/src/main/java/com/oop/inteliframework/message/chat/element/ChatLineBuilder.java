package com.oop.inteliframework.message.chat.element;

import com.oop.inteliframework.commons.util.InteliVersion;
import com.oop.inteliframework.message.ComponentUtil;
import com.oop.inteliframework.message.chat.element.addition.Addition;
import com.oop.inteliframework.message.chat.element.util.ChatLineUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

@UtilityClass
public class ChatLineBuilder {
    public TextComponent buildComponents(ChatLineElement element) {
        AtomicReference<StringBuilder> builder = new AtomicReference<>(new StringBuilder());
        final TextComponent[] component = {Component.empty()};
        ComponentDecoration decoration = new ComponentDecoration();

        for (LineContentElement lineContent : element) {
            @NonNull String text = lineContent.getText();

            Runnable createComponent =
                () -> {
                    TextComponent newComponent = Component.text(builder.toString());
                    builder.set(new StringBuilder());
                    newComponent = decoration.apply(newComponent);
                    component[0] = component[0].append(newComponent);
                };

            char[] chars = text.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char character = chars[i];

                // Check for hex Colors
                if (character == '#' && InteliVersion.isOrAfter(16)) {
                    String hex =
                        ChatLineUtil.getNextOrNull(Arrays.copyOfRange(chars, i + 1, chars.length), 6);
                    if (hex != null) {
                        TextColor color = TextColor.fromHexString("#" + hex);

                        if (i != 0) {
                            if (chars.length > i + 6 && chars[i + 6] == '&') {
                                i += 6;
                                continue;
                            }

                            createComponent.run();
                        }

                        decoration.setColor(color);
                        i += 6;
                        continue;
                    }
                }

                // Check for bukkit colors
                if (character == '&' || character == '\u00a7') {
                    char codeAfter = chars[i + 1];
                    NamedTextColor color = ComponentUtil.bukkitColorToKyori.get(codeAfter);
                    TextDecoration textDecoration = ComponentUtil.bukkitToDecoration.get(codeAfter);

                    if (color == null && textDecoration == null) {
                        i += 1;
                        continue;
                    }

                    if (i != 0 && builder.toString().trim().length() != 0) {
                        if (chars.length > i + 1 && chars[i + 1] == '&') {
                            i += 1;
                            continue;
                        }
                        createComponent.run();
                    }

                    if (color != null) {
                        decoration.setColor(color);
                    }
                    else {
                        decoration.getDecorations().add(textDecoration);
                    }

                    i += 1;
                    continue;
                }

                builder.get().append(character);
            }

            createComponent.run();

            for (Addition<?> addition : lineContent.additionList()) {
                component[0] = addition.apply(component[0]);
            }
        }

        return component[0];
    }

    @Getter
    @ToString
    private static class ComponentDecoration {
        private final List<TextDecoration> decorations = new ArrayList<>();
        private TextColor color = NamedTextColor.WHITE;

        public TextComponent applyColor(TextComponent component) {
            return component.color(color);
        }

        public TextComponent apply(TextComponent component) {
            return applyDecor(applyColor(component));
        }

        public TextComponent applyDecor(TextComponent component) {
            for (TextDecoration decoration : decorations) {
                component = component.decoration(decoration, true);
            }

            return component;
        }

        public void setColor(TextColor color) {
            decorations.clear();
            this.color = color;
        }
    }
}
