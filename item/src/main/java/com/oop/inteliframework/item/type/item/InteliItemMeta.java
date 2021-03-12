package com.oop.inteliframework.item.type.item;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import lombok.NonNull;
import org.bukkit.inventory.meta.ItemMeta;

public class InteliItemMeta extends AbstractInteliItemMeta<ItemMeta, InteliItemMeta> {
  public InteliItemMeta(@NonNull ItemMeta meta) {
    super(meta, s -> new InteliItemMeta(s.getMeta().clone()));
  }

  public InteliItemMeta() {
    this(InteliMaterial.STONE);
  }

  public InteliItemMeta(@NonNull InteliMaterial material) {
    this(material.parseItem().getItemMeta());
  }
}
