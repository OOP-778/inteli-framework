package com.oop.inteliframework.item;

import com.oop.inteliframework.item.type.InteliLore;
import com.oop.inteliframework.item.type.banner.InteliBannerMeta;
import com.oop.inteliframework.item.type.firework.InteliFireworkMeta;
import com.oop.inteliframework.item.type.item.InteliItemMeta;
import com.oop.inteliframework.item.type.leather.InteliLeatherMeta;
import com.oop.inteliframework.item.type.potion.InteliPotionMeta;
import com.oop.inteliframework.item.type.skull.InteliSkullMeta;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;

@UtilityClass
public class SimpleMetaFactory {
  // Default

  public @NonNull InteliItemMeta metaOf() {
    return new InteliItemMeta();
  }

  public @NonNull InteliSkullMeta skullMetaOf() {
    return new InteliSkullMeta();
  }

  public @NonNull InteliPotionMeta potionMetaOf() {
    return new InteliPotionMeta();
  }

  public @NonNull InteliBannerMeta bannerMetaOf() {
    return new InteliBannerMeta();
  }

  public @NonNull InteliFireworkMeta fireworkMetaOf() {
    return new InteliFireworkMeta();
  }

  public @NonNull InteliLeatherMeta leatherArmorMetaOf() {
    return new InteliLeatherMeta();
  }

  // From meta

  public @NonNull InteliItemMeta metaOf(@NonNull ItemMeta meta) {
    return new InteliItemMeta(meta);
  }

  public @NonNull InteliSkullMeta skullMetaOf(@NonNull SkullMeta meta) {
    return new InteliSkullMeta(meta);
  }

  public @NonNull InteliPotionMeta potionMetaOf(@NonNull PotionMeta meta) {
    return new InteliPotionMeta(meta);
  }

  public @NonNull InteliBannerMeta bannerMetaOf(@NonNull BannerMeta meta) {
    return new InteliBannerMeta(meta);
  }

  public @NonNull InteliFireworkMeta fireworkMetaOf(@NonNull FireworkMeta meta) {
    return new InteliFireworkMeta(meta);
  }

  public @NonNull InteliLeatherMeta leatherArmorMetaOf(@NonNull LeatherArmorMeta meta) {
    return new InteliLeatherMeta(meta);
  }

  public @NonNull InteliLore loreOf() {
    return new InteliLore();
  }
}
