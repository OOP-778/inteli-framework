package com.oop.inteliframework.hologram.rule;

import com.oop.inteliframework.hologram.HologramView;
import org.bukkit.entity.Player;

public class RadiusRule implements HologramRule {
    private double radius;
    public RadiusRule(double radius) {
        this.radius = radius;
    }

    @Override
    public boolean canSee(HologramView hologramView, Player player) {
        return hologramView.getBaseLocation().current().distance(player.getLocation()) <= radius;
    }
}
