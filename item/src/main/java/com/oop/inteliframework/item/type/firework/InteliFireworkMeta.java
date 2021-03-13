package com.oop.inteliframework.item.type.firework;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import lombok.NonNull;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;

public class InteliFireworkMeta extends AbstractInteliItemMeta<FireworkMeta, InteliFireworkMeta> {
  public InteliFireworkMeta(@NonNull FireworkMeta meta) {
    super(meta, s -> new InteliFireworkMeta(s.getMeta().clone()));
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
    getMeta().addEffects(effects);
    return this;
  }

  public InteliFireworkMeta power(int power) {
    getMeta().setPower(power);
    return this;
  }

  public InteliFireworkMeta removeEffect(int effectNumber) {
    getMeta().removeEffect(effectNumber);
    return this;
  }
}
