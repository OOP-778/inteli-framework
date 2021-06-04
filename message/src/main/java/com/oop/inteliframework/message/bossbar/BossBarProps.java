package com.oop.inteliframework.message.bossbar;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.kyori.adventure.bossbar.BossBar;

@Builder (toBuilder = true)
@Getter
@Accessors(fluent = true)
@ToString
public class BossBarProps {

  @NonNull @Builder.Default() private BossBar.Overlay overlay = BossBar.Overlay.PROGRESS;

  @NonNull @Builder.Default() private BossBar.Color color = BossBar.Color.RED;

  @Builder.Default() private float percentage = 1;

  @Builder.Default() private long stay = 100;
}
