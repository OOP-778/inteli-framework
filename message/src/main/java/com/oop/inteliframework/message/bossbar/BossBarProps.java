package com.oop.inteliframework.message.bossbar;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.kyori.adventure.bossbar.BossBar;

@Builder
@Getter
@Accessors(fluent = true)
@ToString
public class BossBarProps {

    @NonNull
    private final BossBar.Overlay overlay;

    @NonNull
    private final BossBar.Color color;

    @Builder.Default()
    private final float percentage = 1;

    @Builder.Default()
    private final long stay = 100;

}
