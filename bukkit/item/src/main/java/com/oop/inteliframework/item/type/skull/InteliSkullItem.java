package com.oop.inteliframework.item.type.skull;

import com.oop.inteliframework.commons.util.InteliVersion;
import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import lombok.Getter;
import lombok.NonNull;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class InteliSkullItem extends AbstractInteliItem<InteliSkullMeta, InteliSkullItem> {
  private final String defaultTexture =
      "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19";

  @Getter
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
          nbt.putAndUse(
              "SkullOwner",
              new CompoundTag(),
              skull -> {
                UUID id = UUID.randomUUID();

                if (InteliVersion.isOrAfter(16)) skull.putIntArray("Id", _16_fromUUID(id));
                else skull.putString("Id", id.toString());

                skull.putAndUse(
                    "Properties",
                    new CompoundTag(),
                    propertiesCompound -> {
                      propertiesCompound.putAndUse(
                          "textures",
                          new ListTag<>(CompoundTag.class),
                          texturesList -> {
                            CompoundTag compoundTag = new CompoundTag();
                            compoundTag.putString("Value", this.texture);

                            texturesList.add(compoundTag);
                          });
                    });
              });
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
