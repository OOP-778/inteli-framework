package com.oop.inteliframework.item.type.firework;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;

@Getter
public class InteliFireworkMeta extends AbstractInteliItemMeta<FireworkMeta, InteliFireworkMeta> {

  private List<FireworkEffect> effects = new ArrayList<>();
  private int power = 0;

  public InteliFireworkMeta(@NonNull FireworkMeta meta) {
    super(meta, s -> new InteliFireworkMeta(s.asBukkitMeta().clone()));
  }

  public InteliFireworkMeta() {
    this(InteliMaterial.FIREWORK_ROCKET);
  }

  public InteliFireworkMeta(@NonNull InteliMaterial material) {
    this((FireworkMeta) material.parseItem().getItemMeta());
  }

  public InteliFireworkMeta effect(FireworkEffect effect) {
    return effects(effect);
  }

  public InteliFireworkMeta effects(FireworkEffect... effects) {
    asBukkitMeta().addEffects(effects);
    this.effects.addAll(Arrays.stream(effects).collect(Collectors.toList()));
    return this;
  }

  public InteliFireworkMeta power(int power) {
    asBukkitMeta().setPower(power);
    this.power = power;
    return this;
  }

  public InteliFireworkMeta removeEffect(int effectNumber) {
    asBukkitMeta().removeEffect(effectNumber);
    effects.remove(effectNumber);
    return this;
  }
}
