package com.oop.inteliframework.item.type.banner;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors
public class InteliBannerMeta extends AbstractInteliItemMeta<BannerMeta, InteliBannerMeta> {

  private final @NonNull List<Pattern> patterns = new ArrayList<>();

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
    this.patterns.addAll(Arrays.stream(pattern).collect(Collectors.toList()));
    return this;
  }

  public InteliBannerMeta removePattern(final int patternNumber) {
    asBukkitMeta().removePattern(patternNumber);
    patterns.remove(patternNumber);
    return this;
  }
}
