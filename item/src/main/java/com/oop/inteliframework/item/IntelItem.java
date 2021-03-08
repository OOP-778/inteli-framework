package com.oop.inteliframework.item;

import com.oop.inteliframework.commons.util.Preconditions;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.function.Consumer;

public class IntelItem<T extends ItemMeta> {
    private ItemStack item;
    private T meta;
    private NBTItem nbt;

    private IntelItem(@NonNull ItemStack item) {
        if (item.hasItemMeta())
            this.meta = (T) item.getItemMeta();
        else
            this.meta = (T) Bukkit.getItemFactory().getItemMeta(item.getType());
        this.item = item;
    }

    public IntelItem<T> clone() {
        IntelItem<T> cloned = new IntelItem<>(item.clone());
        return cloned;
    }

    public IntelItem<T> applyMeta(Consumer<T> meta) {
        meta.accept(this.meta);
        return this;
    }

    public static <T extends ItemMeta> IntelItem<T> of(ItemStack item) {
        return new IntelItem<>(item);
    }

    public static IntelItem<BannerMeta> ofBanner(@NonNull ItemStack item) {
        Preconditions.checkArgument(item.getType().name().contains("BANNER"), "Cannot get banner item of a non banner item");
        return new IntelItem<>(item);
    }

    public static IntelItem<PotionMeta> ofPotion(@NonNull ItemStack item) {
        Preconditions.checkArgument(item.getType().name().contains("POTION"), "Cannot get banner item of a non potion item");
        return new IntelItem<>(item);
    }

    public static IntelItem<SkullMeta> ofSkull(@NonNull ItemStack item) {
        Preconditions.checkArgument(item.getType().name().contains("HEAD") || item.getType().name().contains("SKULL"), "Cannot get banner item of a non potion item");
        return new IntelItem<>(item);
    }
}
