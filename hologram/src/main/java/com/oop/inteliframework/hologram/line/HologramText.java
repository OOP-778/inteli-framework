package com.oop.inteliframework.hologram.line;

import com.google.common.collect.Maps;
import com.oop.inteliframework.commons.util.InteliCache;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.hologram.HologramLine;
import com.oop.inteliframework.hologram.animation.AnimationHandler;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class HologramText extends HologramLine<HologramText, String> {
    // Animations
    private final Map<UUID, AnimationHandler> animations = Maps.newConcurrentMap();

    @Setter
    @NonNull
    private Function<Player, String> textSupplier;

    private InteliCache<UUID, String> textCache = InteliCache
            .builder()
            .concurrencyLevel(0)
            .resetExpireAfterAccess(true)
            .expireAfter(2, TimeUnit.SECONDS)
            .build();

    public HologramText(Function<Player, String> textSupplier) {
        this.textSupplier = textSupplier;
    }

    public HologramText(String text) {
        this(() -> text);
    }

    public HologramText(@NonNull Supplier<String> textSupplier) {
        this(player -> textSupplier.get());
    }

    @Override
    public synchronized void update() {
        for (Player viewer : getHologramView().getViewers()) {
            String cachedText = textCache.get(viewer.getUniqueId());
            String suppliedText = textSupplier.apply(viewer);

            if (suppliedText == null) continue;
            if (cachedText != null && cachedText.hashCode() == suppliedText.hashCode()) continue;

            getWrappedArmorStand().outputName(viewer, ChatColor.translateAlternateColorCodes('&', suppliedText));
            textCache.replace(viewer.getUniqueId(), suppliedText);
        }
    }

    @Override
    public void clearData() {
        textCache.clear();
    }

    public void animate() {
        for (Player viewer : getHologramView().getViewers()) {
            AnimationHandler animationHandler = animations.get(viewer.getUniqueId());
            if (animationHandler == null) continue;

            InteliPair<String, Boolean> update = animationHandler.update();
            if (update.getValue())
                getWrappedArmorStand().outputName(viewer, ChatColor.translateAlternateColorCodes('&', update.getKey()));
        }
    }
}
