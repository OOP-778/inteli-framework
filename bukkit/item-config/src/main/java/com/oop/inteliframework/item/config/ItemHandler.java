package com.oop.inteliframework.item.config;

import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.config.property.property.custom.PropertyHandler;
import com.oop.inteliframework.item.comp.InteliMaterial;
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

public class ItemHandler implements PropertyHandler<InteliItem> {

  @Override
  public SerializedProperty toNode(@NonNull InteliItem object) {
    return null;
  }

  @Override
  public InteliItem fromNode(Node node) {

    return null;
  }

  @Override
  public Class<InteliItem> getObjectClass() {
    return InteliItem.class;
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
            node.set(currentNode + ".ambient", effect.isAmbient());
            node.set(currentNode + ".particle", effect.hasParticles());
          });

      serializeEffectType(node, "main-effect", meta.getMainEffect());
    }

    private void serializeEffectType(@NonNull BaseParentNode node, @NonNull String basePath,
        @NonNull
            PotionEffectType type) {
      node.set(basePath + ".type.name", type.getName());
      node.set(basePath + ".type.modifier",
          type.getDurationModifier());
      node.set(basePath + ".type.instant", type.isInstant());
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
          .ifPresent(name -> item.meta().nameSupplier(() -> name));
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
          .ifPresent(texture -> item.texture(texture));

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
          .flatMap(it -> Optional.of(colorOf(it)))
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
                    part.get("type", "Type is not provided!");

                  })
          );

      return item;
    }

    protected AbstractInteliItem<?, ?> deserializePotion(@NonNull BaseParentNode node) {
      throw new UnsupportedOperationException("This deserialization type is not supported!");
    }

    @Nullable
    private InteliMaterial getMaterial(@NonNull BaseParentNode node,
        @Nullable Predicate<InteliMaterial> materialPredicate, @Nullable String errorMessage) {
      final InteliMaterial material = InteliMaterial.valueOf(
          node.get("material", "Material is not provided!").asValue().getAs(String.class)
              .toLowerCase(Locale.ROOT));

      if (materialPredicate != null) {
        if (!materialPredicate.test(material)) {
          throw new IllegalArgumentException(
              Optional.ofNullable(errorMessage).orElse("Material doesn't match predicate!"));
        }
      }

      return material;
    }

    @Nullable
    private InteliMaterial getMaterial(@NonNull BaseParentNode node) {
      return getMaterial(node, null, null);
    }

    @Nullable
    private <T extends Enum<T>> T enumValueOf(@Nullable String name, @NonNull Class<T> enumClass) {
      if (name == null) return null;

      return Enum.valueOf(enumClass, name.toLowerCase(Locale.ROOT));
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
