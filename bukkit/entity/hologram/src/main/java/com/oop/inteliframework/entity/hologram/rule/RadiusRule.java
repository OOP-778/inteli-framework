package com.oop.inteliframework.entity.hologram.rule;

import com.oop.inteliframework.entity.hologram.HologramView;
import lombok.Setter;
import org.bukkit.entity.Player;

public class RadiusRule implements HologramRule {
  @Setter private double radius;

  public RadiusRule(double radius) {
    this.radius = radius;
  }

  @Override
  public boolean canSee(HologramView hologramView, Player player) {
    return hologramView.getBaseLocation().current().distance(player.getLocation()) <= radius;
  }
}
