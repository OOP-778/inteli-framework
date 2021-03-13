package com.oop.inteliframework.item.type.skull;

import com.oop.inteliframework.commons.util.InteliVersion;
import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class InteliSkullItem extends AbstractInteliItem<InteliSkullMeta, InteliSkullItem> {
  private final String defaultTexture =
      "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19";
  private String texture;

  public InteliSkullItem(@NonNull ItemStack itemStack) {
    super(itemStack, s -> new InteliSkullItem(s.asBukkitStack().clone()));

    if (!InteliMaterial.matchMaterial(itemStack).isHead())
      throw new UnsupportedOperationException("Material must be head!");
  }

  public InteliSkullItem texture(String texture) {
    this.texture = texture == null ? defaultTexture : texture;
    applyNBT(
        nbt -> {
          NBTCompound skull = nbt.addCompound("SkullOwner");
          UUID id = UUID.randomUUID();

          if (InteliVersion.isOrAfter(16)) skull.setIntArray("Id", _16_fromUUID(id));
          else skull.setString("Id", id.toString());

          NBTCompoundList compoundList =
              skull.addCompound("Properties").getCompoundList("textures");
          compoundList.clear();

          NBTListCompound textures = compoundList.addCompound();
          textures.setString("Value", texture);
        });

    return this;
  }

  private int[] _16_fromUUID(UUID uuid) {
    long MSB = uuid.getMostSignificantBits(), LSB = uuid.getLeastSignificantBits();
    return new int[] {(int) (MSB >> 32), (int) MSB, (int) (LSB >> 32), (int) LSB};
  }

  @Override
  protected InteliSkullMeta _createMeta() {
    return new InteliSkullMeta(
        (SkullMeta)
            (asBukkitStack().hasItemMeta()
                ? Bukkit.getItemFactory().getItemMeta(asBukkitStack().getType())
                : asBukkitStack().getItemMeta()));
  }
}
