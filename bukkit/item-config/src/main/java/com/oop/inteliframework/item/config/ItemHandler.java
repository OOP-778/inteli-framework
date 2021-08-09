package com.oop.inteliframework.item.config;

import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.config.property.property.custom.PropertyHandler;
import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.comp.InteliPotion;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import com.oop.inteliframework.item.type.InteliLore;
import com.oop.inteliframework.item.type.banner.InteliBannerItem;
import com.oop.inteliframework.item.type.banner.InteliBannerMeta;
import com.oop.inteliframework.item.type.book.InteliBookItem;
import com.oop.inteliframework.item.type.book.InteliBookMeta;
import com.oop.inteliframework.item.type.firework.InteliFireworkItem;
import com.oop.inteliframework.item.type.firework.InteliFireworkMeta;
import com.oop.inteliframework.item.type.item.InteliItem;
import com.oop.inteliframework.item.type.item.InteliItemMeta;
import com.oop.inteliframework.item.type.leather.InteliLeatherItem;
import com.oop.inteliframework.item.type.leather.InteliLeatherMeta;
import com.oop.inteliframework.item.type.potion.InteliPotionItem;
import com.oop.inteliframework.item.type.potion.InteliPotionMeta;
import com.oop.inteliframework.item.type.skull.InteliSkullItem;
import com.oop.inteliframework.item.type.skull.InteliSkullMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class ItemHandler implements PropertyHandler<AbstractInteliItem> {

  private final Serializer serializer = new Serializer();
  private final Deserializer deserializer = new Deserializer();

  @Override
  public SerializedProperty toNode(@NonNull AbstractInteliItem object) {
    final BaseParentNode node = new BaseParentNode();

    serializer.serializeBasic(node, object);

    if (object instanceof InteliBookItem) {
      serializer.serializeBook(node, object);
    } else if (object instanceof InteliSkullItem) {
      serializer.serializeSkull(node, object);
    } else if (object instanceof InteliBannerItem) {
      serializer.serializeBanner(node, object);
    } else if (object instanceof InteliFireworkItem) {
      serializer.serializeFirework(node, object);
    } else if (object instanceof InteliLeatherItem) {
      serializer.serializeLeatherArmor(node, object);
    } else if (object instanceof InteliPotionItem) {
      serializer.serializePotion(node, object);
    }

    return SerializedProperty.of(node);
  }

  @Override
  public AbstractInteliItem fromNode(Node node) {
    final BaseParentNode parentNode = (BaseParentNode) node;
    final InteliMaterial material = deserializer.getMaterial(parentNode);

    final AbstractInteliItem item = deserializer.deserializeBasic(parentNode);
    if (material.isLeatherArmor()) {
      item.meta().appendValues(deserializer.deserializeLeatherArmor(parentNode).meta());
    } else if (material.isBanner()) {
      item.meta().appendValues(deserializer.deserializeBanner(parentNode).meta());
    } else if (material.isBook()) {
      item.meta().appendValues(deserializer.deserializeBook(parentNode).meta());
    } else if (material.isPotion()) {
      item.meta().appendValues(deserializer.deserializePotion(parentNode).meta());
    } else if (material.isHead()) {
      item.meta().appendValues(deserializer.deserializeSkull(parentNode).meta());
    } else if (material == InteliMaterial.FIREWORK_ROCKET) {
      item.meta().appendValues(deserializer.deserializeFirework(parentNode).meta());
    }

    return item;
  }

  @Override
  public Class<AbstractInteliItem> getObjectClass() {
    return AbstractInteliItem.class;
  }

  protected static class Serializer {

    protected void serializeBasic(@NonNull BaseParentNode node,
        @NonNull AbstractInteliItem<?, ?> _item) {
      if (!(_item instanceof InteliItem)) {
        return;
      }

      final InteliItem item = (InteliItem) _item;

      // Write material
      node.set("material", item.material().name().toLowerCase(Locale.ROOT));

      final InteliItemMeta meta = item.meta();
      if (meta == null) {
        return;
      }

      // Set the display name
      final String displayName = meta.name();
      if (displayName != null) {
        node.set("display-name", displayName);
      }

      // Set if glowing
      if (meta.glowing()) {
        node.set("glow", true);
      }

      // Set lore
      node.set("lore", meta.lore().raw());
    }

    protected void serializeBook(@NonNull BaseParentNode node,
        @NonNull AbstractInteliItem<?, ?> _item) {
      if (!(_item instanceof InteliBookItem)) {
        return;
      }

      final InteliBookItem bookItem = (InteliBookItem) _item;

      final InteliBookMeta meta = bookItem.meta();

      if (meta == null) {
        return;
      }

      if (meta.getAuthor() != null) {
        node.set("author", meta.getAuthor());
      }

      if (meta.getTitle() != null) {
        node.set("title", meta.getTitle());
      }

      final List<String> pages = meta.getPages();

      for (int i = 0; i < pages.size(); i++) {
        node.set("pages." + i, pages.get(i));
      }
    }

    protected void serializeSkull(@NonNull BaseParentNode node,
        @NonNull AbstractInteliItem<?, ?> _item) {
      if (!(_item instanceof InteliSkullItem)) {
        return;
      }

      final InteliSkullItem skullItem = (InteliSkullItem) _item;

      final InteliSkullMeta meta = skullItem.meta();
      if (meta == null) {
        return;
      }

      final OfflinePlayer provider = meta.getSkullProvider();

      // Check is provider not null, if not, set node and return
      if (provider != null) {
        node.set("owner", provider.getName());
        return;
      }

      if (StringUtils.isBlank(skullItem.getTexture()) || skullItem.getTexture() == null) {
        return;
      }

      node.set("texture", skullItem.getTexture());
    }

    protected void serializeBanner(@NonNull BaseParentNode node,
        @NonNull AbstractInteliItem<?, ?> _item) {
      if (!(_item instanceof InteliBannerItem)) {
        return;
      }

      final InteliBannerItem bannerItem = (InteliBannerItem) _item;

      final InteliBannerMeta meta = bannerItem.meta();
      if (meta == null) {
        return;
      }

      this.<Pattern, Pattern>serializeList(node, "pattern.%s", meta.getPatterns(), null,
          (pattern, currentNode) -> {
            node.set(currentNode + ".type", pattern.getPattern().name().toLowerCase(Locale.ROOT));
            node.set(currentNode + ".color", pattern.getColor().name().toLowerCase(Locale.ROOT));
          });
    }

    protected void serializeLeatherArmor(@NonNull BaseParentNode node,
        @NonNull AbstractInteliItem<?, ?> _item) {
      if (!(_item instanceof InteliLeatherItem)) {
        return;
      }

      final InteliLeatherItem leatherItem = (InteliLeatherItem) _item;

      final InteliLeatherMeta meta = leatherItem.meta();
      if (meta == null) {
        return;
      }
      if (meta.getColor() == null) {
        return;
      }

      final Color color = meta.getColor();
      node.set("dye", color.getRed() + ";" + color.getGreen() + ";" + color.getBlue());
    }

    protected void serializeFirework(@NonNull BaseParentNode node,
        @NonNull AbstractInteliItem<?, ?> _item) {
      if (!(_item instanceof InteliFireworkItem)) {
        return;
      }

      final InteliFireworkItem fireworkItem = (InteliFireworkItem) _item;

      final InteliFireworkMeta meta = fireworkItem.meta();
      if (meta == null) {
        return;
      }

      node.set("power", meta.getPower());

      this.<FireworkEffect, FireworkEffect>serializeList(node, "effects.%s", meta.getEffects(),
          null,
          (fireworkEffect, currentNode) -> {
            node.set(currentNode + ".type", fireworkEffect.getType().name());

            node.set(currentNode + ".flicker", fireworkEffect.hasFlicker());
            node.set(currentNode + ".trail", fireworkEffect.hasTrail());

            this.serializeList(node, currentNode + ".colors.%s", fireworkEffect.getColors(),
                color -> color.getRed() + ";" + color.getGreen() + ";" + color.getBlue(), null);
            this.serializeList(node, currentNode + ".fade-colors.%s",
                fireworkEffect.getFadeColors(),
                color -> color.getRed() + ";" + color.getGreen() + ";" + color.getBlue(), null);
          });
    }

    protected void serializePotion(@NonNull BaseParentNode node,
        @NonNull AbstractInteliItem<?, ?> _item) {
      if (!(_item instanceof InteliPotionItem)) {
        return;
      }

      final InteliPotionItem potionItem = (InteliPotionItem) _item;

      final InteliPotionMeta meta = potionItem.meta();
      if (potionItem.meta() == null) {
        return;
      }

      this.<PotionEffect, PotionEffect>serializeList(node, "effects.%s",
          meta.getEffects(), null, (effect, currentNode) -> {
            serializeEffectType(node, currentNode, effect.getType());

            node.set(currentNode + ".amplifier", effect.getAmplifier());
            node.set(currentNode + ".duration", effect.getDuration());
          });
    }

    private void serializeEffectType(@NonNull BaseParentNode node, @NonNull String basePath,
        @NonNull
            PotionEffectType type) {
      node.set(basePath + ".type", type.getName());
    }

    private <T, S> void serializeList(@NonNull BaseParentNode node, @NonNull String basePath,
        @NonNull List<T> input, @Nullable Function<T, String> toStringFunction,
        @Nullable BiConsumer<S, String> postSet) {
      final AtomicInteger counter = new AtomicInteger(0);

      if (toStringFunction == null) {
        Objects.requireNonNull(postSet, "When toStringFunction null, postSet cannot be null!");

        input.forEach(
            it -> postSet.accept((S) it, String.format(basePath, counter.getAndIncrement())));
      } else {
        input
            .stream()
            .map(toStringFunction)
            .forEach(it -> {
              final String formattedNode = String.format(basePath, counter.getAndIncrement());

              node.set(String.format(formattedNode, counter.getAndIncrement()), it);

              if (postSet != null) {
                postSet.accept((S) it, formattedNode);
              }
            });
      }
    }
  }

  protected static class Deserializer {

    protected AbstractInteliItem<?, ?> deserializeBasic(@NonNull BaseParentNode node) {
      final InteliMaterial material = getMaterial(node);

      final InteliItem item = new InteliItem(material.parseItem());

      node.getAsOptional("display-name")
          .map(_node -> _node.asValue().getAs(String.class))
          .ifPresent(name -> item.meta().name(name));
      node
          .getAsOptional("glow")
          .map(_node -> _node.asValue().getAs(boolean.class))
          .ifPresent(glowing -> item.meta().glowing(glowing));

      node.getAsOptional("lore")
          .map(_node -> _node.asValue().getAsListOf(String.class))
          .ifPresent(lore -> item.meta().lore(new InteliLore(lore)));

      return item;
    }

    protected AbstractInteliItem<?, ?> deserializeBook(@NonNull BaseParentNode node) {
      final InteliBookItem item = new InteliBookItem(getMaterial(node).parseItem());

      node.getAsOptional("author")
          .map(_node -> _node.asValue().getAs(String.class))
          .ifPresent(name -> item.meta().author(name));
      node
          .getAsOptional("title")
          .map(_node -> _node.asValue().getAs(boolean.class))
          .ifPresent(glowing -> item.meta().glowing(glowing));

      node.getAsOptional("pages")
          .map(Node::asParent)
          .ifPresent(parent ->
              parent.map(NodeIterator.ALL)
                  .forEach((page, pageNode) -> {
                    if (StringUtils.isNumeric(page)) {
                      item.meta()
                          .setPage(Integer.parseInt(page),
                              String.join("\n", pageNode.asValue().getAsListOf(String.class)));
                    }
                  })
          );

      return item;
    }

    protected AbstractInteliItem<?, ?> deserializeSkull(@NonNull BaseParentNode node) {
      final InteliSkullItem item = new InteliSkullItem(getMaterial(node).parseItem());

      node.getAsOptional("head")
          .map(_node -> _node.asValue().getAs(String.class))
          .ifPresent(head -> item.meta().uuid(Bukkit.getOfflinePlayer(head)));
      node
          .getAsOptional("texture")
          .map(_node -> _node.asValue().getAs(String.class))
          .ifPresent(item::texture);

      return item;
    }

    protected AbstractInteliItem<?, ?> deserializeBanner(@NonNull BaseParentNode node) {
      final InteliBannerItem item = new InteliBannerItem(getMaterial(node).parseItem());

      node.getAsOptional("pattern")
          .map(Node::asParent)
          .map(parentNode -> parentNode
              .map(NodeIterator.ALL).values()
              .stream()
              .map(Node::asParent))
          .ifPresent(parentNode ->
              parentNode
                  .forEach(part -> {
                    final PatternType type = enumValueOf(part
                        .get("type", "Pattern type is not provided! (Banner Item)")
                        .asValue()
                        .getAs(String.class), PatternType.class);

                    final DyeColor color = enumValueOf(part
                        .get("color", "Color is not provided! (Banner Item)")
                        .asValue()
                        .getAs(String.class), DyeColor.class);

                    item.meta()
                        .pattern(new Pattern(color, type));
                  })
          );

      return item;
    }

    protected AbstractInteliItem<?, ?> deserializeLeatherArmor(@NonNull BaseParentNode node) {
      final InteliLeatherItem item = new InteliLeatherItem(getMaterial(node).parseItem());

      node.getAsOptional("dye")
          .map(it -> it.asValue().getAs(String.class))
          .flatMap(it -> Optional.ofNullable(colorOf(it)))
          .ifPresent(color -> item.meta().color(color));

      return item;
    }

    protected AbstractInteliItem<?, ?> deserializeFirework(@NonNull BaseParentNode node) {
      final InteliFireworkItem item = new InteliFireworkItem(getMaterial(node).parseItem());

      node.getAsOptional("power")
          .map(it -> it.asValue().getAs(int.class))
          .ifPresent(power -> item.meta().power(power));

      node.getAsOptional("effects")
          .map(Node::asParent)
          .map(parentNode -> parentNode
              .map(NodeIterator.ALL).values()
              .stream()
              .map(Node::asParent))
          .ifPresent(parentNode ->
              parentNode
                  .forEach(part -> {
                    item.meta().effect(FireworkEffect.builder()
                        .withColor(deserializeColors(part.get("colors", "Colors cannot be empty!")))
                        .withFade(deserializeColors(
                            part.get("fade-colors", "Fade colors cannot be empty!")))
                        .flicker(part.getAsOptional("flicker")
                            .map(it -> it.asValue().getAs(boolean.class)).orElse(false))
                        .trail(part.getAsOptional("trail")
                            .map(it -> it.asValue().getAs(boolean.class)).orElse(false))
                        .with(enumValueOf(
                            part.get("type", "Type is not provided!").asValue().getAs(String.class),
                            FireworkEffect.Type.class))
                        .build());
                  })
          );

      return item;
    }

    protected AbstractInteliItem<?, ?> deserializePotion(@NonNull BaseParentNode node) {
      final InteliPotionItem item = new InteliPotionItem(getMaterial(node).parseItem());

      node.get("effects", "Effects cannot be empty!")
          .asParent()
          .map(NodeIterator.ALL)
          .values()
          .stream()
          .map(Node::asParent)
          .forEach(data -> {

            item.meta()
                .customEffect(deserializeIPotion((BaseParentNode) data),
                    data.get("amplifier").asValue().getAs(int.class),
                    data.get("duration").asValue().getAs(int.class),
                    false);

          });

      return item;
    }

    private List<Color> deserializeColors(@NonNull Node node) {
      final List<Color> colors = new ArrayList<>();

      node.asParent()
          .map(NodeIterator.ALL)
          .values().stream().map(it -> it.asValue().getAs(String.class))
          .forEach(data -> colors.add(colorOf(data)));

      return colors;
    }

    private InteliMaterial getMaterial(@NonNull BaseParentNode node,
        @Nullable Predicate<InteliMaterial> materialPredicate, @Nullable String errorMessage) {
      final InteliMaterial material = InteliMaterial.matchMaterial(
          node.get("material", "Material is not provided!").asValue().getAs(String.class)
              .toUpperCase(Locale.ROOT));

      if (materialPredicate != null) {
        if (!materialPredicate.test(material)) {
          throw new IllegalArgumentException(
              Optional.ofNullable(errorMessage).orElse("Material doesn't match predicate!"));
        }
      }

      return material;
    }

    private InteliMaterial getMaterial(@NonNull BaseParentNode node) {
      return getMaterial(node, null, null);
    }

    @Nullable
    private <T extends Enum<T>> T enumValueOf(@Nullable String name, @NonNull Class<T> enumClass) {
      if (name == null) {
        return null;
      }

      return Enum.valueOf(enumClass, name.toUpperCase(Locale.ROOT));
    }

    private InteliPotion deserializeIPotion(@NonNull BaseParentNode node) {
      return InteliPotion.valueOf(
          node.get("type", "Type is not provided!").asValue().getAs(String.class));
    }

    @Nullable
    private Color colorOf(@NonNull String path) {
      final String[] strColor = path.split(";");

      Color color = null;
      try {
        color = Color.fromRGB(
            Integer.parseInt(strColor[0]),
            Integer.parseInt(strColor[1]),
            Integer.parseInt(strColor[2])
        );
      } catch (NumberFormatException ignored) {
      }

      return color;
    }

  }

}
