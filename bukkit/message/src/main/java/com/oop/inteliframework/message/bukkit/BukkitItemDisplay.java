package com.oop.inteliframework.message.bukkit;

import com.oop.inteliframework.bukkit.nbt.NBTItem;
import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.message.Replacer;
import com.oop.inteliframework.message.api.ItemDisplay;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
@Setter
public class BukkitItemDisplay implements ItemDisplay, Cloneable {
  private @NonNull ItemStack item;

  public BukkitItemDisplay(@NonNull ItemStack item) {
    this.item = item.clone();
  }

  @Override
  public HoverEvent<HoverEvent.ShowItem> toHoverEvent() {
    Preconditions.checkArgument(
        item.getType() != Material.AIR, "The item must not be air to show!");
    NBTItem nbtItem = new NBTItem(item);
    return HoverEvent.showItem(
        Key.key("minecraft:" + item.getType().name().toLowerCase()),
        item.getAmount(),
        BinaryTagHolder.of(nbtItem.toString()));
  }

  @Override
  public ItemDisplay clone() {
    return new BukkitItemDisplay(item.clone());
  }

  @Override
  public ItemDisplay replace(Replacer replacer) {
    if (!item.hasItemMeta()) return this;

    ItemMeta meta = item.getItemMeta();
    if (meta.hasDisplayName()) {
      meta.setDisplayName(replacer.accept(meta.getDisplayName()));
    }

    if (meta.hasLore()) {
      meta.setLore(replacer.accept(meta.getLore()));
    }
    item.setItemMeta(meta);
    return this;
  }
}
