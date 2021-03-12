package com.oop.inteliframework.item;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.banner.InteliBannerItem;
import com.oop.inteliframework.item.type.firework.InteliFireworkItem;
import com.oop.inteliframework.item.type.item.InteliItem;
import com.oop.inteliframework.item.type.leather.InteliLeatherItem;
import com.oop.inteliframework.item.type.potion.InteliPotionItem;
import com.oop.inteliframework.item.type.skull.InteliSkullItem;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class SimpleItemFactory {
  // Material

  public @NonNull InteliItem itemOf(@NonNull InteliMaterial material) {
    return itemOf(material.parseItem());
  }

  public @NonNull InteliSkullItem skullOf(@NonNull InteliMaterial material) {
    return skullOf(material.parseItem());
  }

  public @NonNull InteliPotionItem potionOf(@NonNull InteliMaterial material) {
    return potionOf(material.parseItem());
  }

  public @NonNull InteliBannerItem bannerOf(@NonNull InteliMaterial material) {
    return bannerOf(material.parseItem());
  }

  public @NonNull InteliFireworkItem fireworkOf(@NonNull InteliMaterial material) {
    return fireworkOf(material.parseItem());
  }

  public @NonNull InteliLeatherItem leatherArmorOf(@NonNull InteliMaterial material) {
    return leatherArmorOf(material.parseItem());
  }

  // Item stack

  public @NonNull InteliItem itemOf(@NonNull ItemStack stack) {
    return new InteliItem(stack);
  }

  public static @NonNull InteliSkullItem skullOf(@NonNull ItemStack stack) {
    return new InteliSkullItem(stack);
  }

  public @NonNull InteliPotionItem potionOf(@NonNull ItemStack stack) {
    return new InteliPotionItem(stack);
  }

  public @NonNull InteliBannerItem bannerOf(@NonNull ItemStack stack) {
    return new InteliBannerItem(stack);
  }

  public @NonNull InteliFireworkItem fireworkOf(@NonNull ItemStack stack) {
    return new InteliFireworkItem(stack);
  }

  public @NonNull InteliLeatherItem leatherArmorOf(@NonNull ItemStack stack) {
    return new InteliLeatherItem(stack);
  }
}
