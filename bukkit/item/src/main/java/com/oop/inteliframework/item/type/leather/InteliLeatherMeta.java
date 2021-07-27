package com.oop.inteliframework.item.type.leather;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

@Getter
public class InteliLeatherMeta extends AbstractInteliItemMeta<LeatherArmorMeta, InteliLeatherMeta> {

  private @Nullable Color color;

  public InteliLeatherMeta(@NonNull LeatherArmorMeta meta) {
    super(meta, s -> new InteliLeatherMeta(s.asBukkitMeta().clone()));
  }

  public InteliLeatherMeta() {
    this(InteliMaterial.LEATHER_HELMET);
  }

  public InteliLeatherMeta(@NonNull InteliMaterial material) {
    this((LeatherArmorMeta) material.parseItem().getItemMeta());
  }

  public InteliLeatherMeta color(Color color) {
    asBukkitMeta().setColor(color);
    this.color = color;
    return this;
  }
}
