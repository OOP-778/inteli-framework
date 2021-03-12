package com.oop.inteliframework.item.type.potion;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.comp.InteliPotion;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteliPotionMeta extends AbstractInteliItemMeta<PotionMeta, InteliPotionMeta> {
  public InteliPotionMeta(@NonNull PotionMeta meta) {
    super(meta, s -> new InteliPotionMeta(s.getMeta().clone()));
  }

  public InteliPotionMeta() {
    this(InteliMaterial.POTION);
  }

  public InteliPotionMeta(@NonNull InteliMaterial material) {
    this((PotionMeta) material.parseItem().getItemMeta());
  }

  public InteliPotionMeta basePotionData(@NotNull PotionData data) {
    getMeta().setBasePotionData(data);
    return this;
  }

  public InteliPotionMeta customEffect(
      @NotNull InteliPotion effect, int duration, int amplifier, boolean overwrite) {
    getMeta().addCustomEffect(effect.parsePotion(duration, amplifier), overwrite);
    return this;
  }

  public InteliPotionMeta removeCustomEffect(@NotNull InteliPotion type) {
    getMeta().removeCustomEffect(type.parsePotionEffectType());
    return this;
  }

  @Deprecated
  public InteliPotionMeta mainEffect(@NotNull InteliPotion type) {
    getMeta().setMainEffect(type.parsePotionEffectType());
    return this;
  }

  public InteliPotionMeta color(@Nullable Color color) {
    getMeta().setColor(color);
    return this;
  }
}
