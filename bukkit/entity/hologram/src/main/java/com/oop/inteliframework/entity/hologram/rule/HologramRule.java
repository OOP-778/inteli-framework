package com.oop.inteliframework.entity.hologram.rule;

import com.oop.inteliframework.entity.hologram.HologramView;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface HologramRule {
  boolean canSee(HologramView hologramView, Player player);
}
