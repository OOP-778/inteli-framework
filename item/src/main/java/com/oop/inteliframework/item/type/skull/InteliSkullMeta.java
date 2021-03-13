package com.oop.inteliframework.item.type.skull;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

public class InteliSkullMeta extends AbstractInteliItemMeta<SkullMeta, InteliSkullMeta> {
  public InteliSkullMeta(@NonNull SkullMeta meta) {
    super(meta, s -> new InteliSkullMeta(s.asBukkitMeta().clone()));
  }

  public InteliSkullMeta() {
    this(InteliMaterial.PLAYER_HEAD);
  }

  public InteliSkullMeta(@NonNull InteliMaterial material) {
    this((SkullMeta) material.parseItem().getItemMeta());
  }

  public InteliSkullMeta uuid(Player player) {
    SkullMeta itemMeta = asBukkitMeta();
    itemMeta.setOwner(player.getName());
    return this;
  }
}
