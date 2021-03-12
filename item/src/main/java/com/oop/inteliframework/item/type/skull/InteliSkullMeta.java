package com.oop.inteliframework.item.type.skull;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

public class InteliSkullMeta extends AbstractInteliItemMeta<SkullMeta, InteliSkullMeta> {
  private final String defaultTexture =
      "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19";
  private String texture;

  public InteliSkullMeta(@NonNull SkullMeta meta) {
    super(meta, s -> new InteliSkullMeta(s.getMeta().clone()));
  }

  public InteliSkullMeta() {
    this(InteliMaterial.PLAYER_HEAD);
  }

  public InteliSkullMeta(@NonNull InteliMaterial material) {
    this((SkullMeta) material.parseItem().getItemMeta());
  }

  public InteliSkullMeta uuid(Player player) {
    SkullMeta itemMeta = getMeta();
    itemMeta.setOwner(player.getName());
    return this;
  }
}
