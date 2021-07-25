package com.oop.inteliframework.message;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.craftbukkit.BukkitComponentSerializer;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ComponentUtil {
  public static final Map<Character, NamedTextColor> bukkitColorToKyori = new HashMap<>();
  public static final Map<Character, TextDecoration> bukkitToDecoration = new HashMap<>();

  static {
    bukkitColorToKyori.put('0', NamedTextColor.BLACK);
    bukkitColorToKyori.put('1', NamedTextColor.DARK_BLUE);
    bukkitColorToKyori.put('2', NamedTextColor.DARK_GREEN);
    bukkitColorToKyori.put('3', NamedTextColor.DARK_AQUA);
    bukkitColorToKyori.put('4', NamedTextColor.DARK_RED);
    bukkitColorToKyori.put('5', NamedTextColor.DARK_PURPLE);
    bukkitColorToKyori.put('6', NamedTextColor.GOLD);
    bukkitColorToKyori.put('7', NamedTextColor.GRAY);
    bukkitColorToKyori.put('8', NamedTextColor.DARK_GRAY);
    bukkitColorToKyori.put('9', NamedTextColor.BLUE);
    bukkitColorToKyori.put('a', NamedTextColor.GREEN);
    bukkitColorToKyori.put('b', NamedTextColor.AQUA);
    bukkitColorToKyori.put('c', NamedTextColor.RED);
    bukkitColorToKyori.put('d', NamedTextColor.LIGHT_PURPLE);
    bukkitColorToKyori.put('e', NamedTextColor.YELLOW);
    bukkitColorToKyori.put('f', NamedTextColor.WHITE);

    bukkitToDecoration.put('k', TextDecoration.OBFUSCATED);
    bukkitToDecoration.put('l', TextDecoration.BOLD);
    bukkitToDecoration.put('m', TextDecoration.STRIKETHROUGH);
    bukkitToDecoration.put('n', TextDecoration.UNDERLINED);
    bukkitToDecoration.put('o', TextDecoration.ITALIC);
  }

  public Component listedTextOf(String... lines) {
    TextComponent component = Component.text("");
    for (int i = 0; i < lines.length; i++) {
      component = component.append(colorizeFromBukkit(lines[i]));
      if ((i + 1) != lines.length) {
        component = component.append(Component.newline());
      }
    }

    return component;
  }

  public Component colorizeFromBukkit(String content) {
    return BukkitComponentSerializer.legacy().deserialize(StringUtils.replace(content, "&", "§"));
  }

  public String contentFromComponent(Component component) {
    return StringUtils.replace(BukkitComponentSerializer.legacy().serialize(component), "§", "&");
  }
}
