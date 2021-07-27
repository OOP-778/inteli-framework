package com.oop.inteliframework.item.type.skull;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

@Getter
public class InteliSkullMeta extends AbstractInteliItemMeta<SkullMeta, InteliSkullMeta> {

  private @Nullable OfflinePlayer skullProvider;

  public InteliSkullMeta(@NonNull SkullMeta meta) {
    super(meta, s -> new InteliSkullMeta(s.asBukkitMeta().clone()));
  }

  public InteliSkullMeta() {
    this(InteliMaterial.PLAYER_HEAD);
  }

  public InteliSkullMeta(@NonNull InteliMaterial material) {
    this((SkullMeta) material.parseItem().getItemMeta());
  }

  public InteliSkullMeta uuid(@NonNull OfflinePlayer player) {
    SkullMeta itemMeta = asBukkitMeta();
    itemMeta.setOwner(player.getName());
    this.skullProvider = player;
    return this;
  }
}
