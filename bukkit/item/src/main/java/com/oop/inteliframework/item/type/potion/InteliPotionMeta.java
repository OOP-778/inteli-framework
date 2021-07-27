package com.oop.inteliframework.item.type.potion;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.comp.InteliPotion;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Getter
public class InteliPotionMeta extends AbstractInteliItemMeta<PotionMeta, InteliPotionMeta> {

  private final List<PotionEffect> effects = new ArrayList<>();
  private PotionEffectType mainEffect;

  public InteliPotionMeta(@NonNull PotionMeta meta) {
    super(meta, s -> new InteliPotionMeta(s.asBukkitMeta().clone()));
  }

  public InteliPotionMeta() {
    this(InteliMaterial.POTION);
  }

  public InteliPotionMeta(@NonNull InteliMaterial material) {
    this((PotionMeta) material.parseItem().getItemMeta());
  }

  public InteliPotionMeta customEffect(
      @NotNull InteliPotion inteliPotion, int duration, int amplifier, boolean overwrite) {
    final PotionEffect effect = inteliPotion.parsePotion(duration, amplifier);

    asBukkitMeta().addCustomEffect(effect, overwrite);

    if (overwrite) {
      removeCustomEffect(inteliPotion);
    }

    effects.add(effect);
    return this;
  }

  public InteliPotionMeta removeCustomEffect(@NotNull InteliPotion type) {
    asBukkitMeta().removeCustomEffect(type.parsePotionEffectType());
    effects.removeIf(it -> it.getType().equals(type.parsePotionEffectType()));
    return this;
  }

  @Deprecated
  public InteliPotionMeta mainEffect(@NotNull InteliPotion type) {
    final PotionEffectType effectType = type.parsePotionEffectType();

    asBukkitMeta().setMainEffect(effectType);
    mainEffect = effectType;
    return this;
  }
}
