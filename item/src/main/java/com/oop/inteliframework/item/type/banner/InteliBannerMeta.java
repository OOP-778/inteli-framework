package com.oop.inteliframework.item.type.banner;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@Accessors
public class InteliBannerMeta extends AbstractInteliItemMeta<BannerMeta, InteliBannerMeta> {

  public InteliBannerMeta(@NonNull BannerMeta meta) {
    super(meta, s -> new InteliBannerMeta(s.asBukkitMeta()));
  }

  public InteliBannerMeta() {
    this(InteliMaterial.BLACK_BANNER);
  }

  public InteliBannerMeta(@NonNull InteliMaterial material) {
    this((BannerMeta) material.parseItem().getItemMeta());
  }

  public InteliBannerMeta pattern(final @NotNull Pattern pattern) {
    return patterns(pattern);
  }

  public InteliBannerMeta patterns(final @NotNull Pattern... pattern) {
    asBukkitMeta().setPatterns(Arrays.asList(pattern));
    return this;
  }

  public InteliBannerMeta removePattern(final int patternNumber) {
    asBukkitMeta().removePattern(patternNumber);
    return this;
  }
}
