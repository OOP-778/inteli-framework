package com.oop.inteliframework.hologram.rule;
import com.oop.inteliframework.hologram.HologramView;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface HologramRule {
    boolean canSee(HologramView hologramView, Player player);
}
