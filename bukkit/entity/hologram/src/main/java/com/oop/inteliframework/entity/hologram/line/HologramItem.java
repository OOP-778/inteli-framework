package com.oop.inteliframework.entity.hologram.line;

import com.oop.inteliframework.commons.util.InteliCache;
import com.oop.inteliframework.entity.commons.UpdateableObject;
import com.oop.inteliframework.entity.hologram.HologramLine;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class HologramItem extends HologramLine<HologramItem, ItemStack> {
  private final InteliCache<UUID, Integer> itemCache =
      InteliCache.builder()
          .concurrencyLevel(0)
          .expireAfter(2, TimeUnit.SECONDS)
          .resetExpireAfterAccess(true)
          .build();
  private UpdateableObject<Function<Player, ItemStack>> itemStackSupplier;

  public HologramItem(Function<Player, ItemStack> itemStackSupplier) {
    this.itemStackSupplier = new UpdateableObject<>(itemStackSupplier);
  }

  public HologramItem(Supplier<ItemStack> itemStackSupplier) {
    this(player -> itemStackSupplier.get());
  }

  public HologramItem(ItemStack itemStack) {
    this(() -> itemStack);
  }

  @Override
  public synchronized void preUpdate() {
    if (wrappedArmorStand == null) location.set(location.get().clone().add(0.0, 0.8, 0.0));

    super.preUpdate();
  }

  @Override
  public synchronized void update() {
    if (location.isUpdated()) location.set(location.current().clone().add(0.0, 0.8, 0.0));

    for (Player viewer : getHologramView().getViewers()) {
      Integer cachedItemHash = itemCache.get(viewer.getUniqueId()).orElse(null);
      ItemStack suppliedItem = itemStackSupplier.get().apply(viewer);

      if (suppliedItem == null) suppliedItem = new ItemStack(Material.STONE);

      ItemStack finalSuppliedItem = suppliedItem;
      Runnable output =
          () -> {
            wrappedArmorStand.outputItem(viewer, finalSuppliedItem);
          };

      if (cachedItemHash == null) {
        itemCache.put(viewer.getUniqueId(), suppliedItem.hashCode());
        output.run();
        continue;
      }

      if (cachedItemHash == suppliedItem.hashCode()) continue;
      output.run();

      itemCache.remove(viewer.getUniqueId());
      itemCache.put(viewer.getUniqueId(), suppliedItem.hashCode());
      wrappedArmorStand.update(viewer);
    }
  }

  @Override
  protected void armorStandCreate() {
    wrappedArmorStand.setLocation(location.current().clone().add(0.0, 0.8, 0.0));
    wrappedArmorStand.setCustomNameVisibility(false);
    wrappedArmorStand.setupItem();
  }

  @Override
  protected void handleRemove(Player player) {
    super.handleRemove(player);
    itemCache.remove(player.getUniqueId());
  }

  @Override
  public void clearData() {
    itemCache.clear();
  }
}
