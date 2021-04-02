package com.oop.inteliframework.message.bossbar;

import com.oop.inteliframework.message.api.InteliMessage;
import com.oop.inteliframework.task.SimpleTaskFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
@ToString
public class InteliBossBarMessage implements InteliMessage<InteliBossBarMessage> {

  private final @NonNull BossBarProps props;
  private final Component text;

  @Override
  public InteliBossBarMessage replace(Consumer<TextReplacementConfig.Builder> builderConsumer) {
    return new InteliBossBarMessage(props, text.replaceText(builderConsumer));
  }

  @Override
  public Component toComponent() {
    return text;
  }

  @Override
  public void send(Audience audience) {
    BossBar bossBar =
        BossBar.bossBar(text, props.percentage(), props.color(), props.overlay());

    audience.showBossBar(bossBar);
    SimpleTaskFactory.later(
        $ -> audience.hideBossBar(bossBar), props.stay(), TimeUnit.MILLISECONDS);
  }
}
