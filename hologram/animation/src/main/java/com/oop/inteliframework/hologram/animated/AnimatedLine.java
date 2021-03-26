package com.oop.inteliframework.hologram.animated;

import com.oop.inteliframework.animation.AnimatedText;
import com.oop.inteliframework.animation.AnimationParser;
import com.oop.inteliframework.commons.util.InteliCache;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.hologram.HologramLine;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class AnimatedLine extends HologramLine<AnimatedLine, String> {

  protected InteliCache<UUID, AnimationCache> textCache =
      InteliCache.builder()
          .concurrencyLevel(1)
          .resetExpireAfterAccess(true)
          .expireAfter(10, TimeUnit.SECONDS)
          .build();
  private @Setter @NonNull Function<Player, String> textSupplier;

  public AnimatedLine(@NotNull Function<Player, String> textSupplier) {
    this.textSupplier = textSupplier;
  }

  @Override
  public synchronized void update() {
    for (Player viewer : getHologramView().getViewers()) {
      AnimationCache animationCache =
          textCache.getIfAbsent(viewer.getUniqueId(), AnimationCache::new);

      boolean contentChanged = false;
      if (animationCache.getLastRequested() == null) {
        animationCache.setLastRequested(textSupplier.apply(viewer));
        contentChanged = true;

      } else {
        String apply = textSupplier.apply(viewer);
        if (!apply.equals(animationCache.getLastRequested())) {
          contentChanged = true;
          animationCache.setLastRequested(apply);
        }
      }

      if (contentChanged || animationCache.getAnimatedText() == null) {
        AnimatedText parse = AnimationParser.parse(animationCache.getLastRequested());
        animationCache.setAnimatedText(parse);
      }

      InteliPair<String, Boolean> update = animationCache.getAnimatedText().update();
      if (update.getValue())
        getWrappedArmorStand()
                .outputName(viewer, ChatColor.translateAlternateColorCodes('&', update.getKey()));
    }
  }

  @Override
  public void clearData() {
    textCache.clear();
  }

  @Override
  protected void handleRemove(Player player) {
    super.handleRemove(player);
    textCache.remove(player.getUniqueId());
  }
}
