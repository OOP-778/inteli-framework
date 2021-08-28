package com.oop.inteliframework.message.bossbar;

import com.oop.inteliframework.message.ComponentUtil;
import com.oop.inteliframework.message.Replacer;
import com.oop.inteliframework.message.api.InteliMessage;
import com.oop.inteliframework.task.SimpleTaskFactory;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

@Getter
@AllArgsConstructor
@ToString
public class InteliBossBarMessage implements InteliMessage<InteliBossBarMessage> {

  private final @NonNull BossBarProps props;
  private Component text;

  @Override
  public Component toComponent() {
    return text;
  }

  @Override
  public void send(Audience audience) {
    BossBar bossBar =
        BossBar.bossBar(toComponent(), props.percentage(), props.color(), props.overlay());

    audience.showBossBar(bossBar);
    SimpleTaskFactory.later($ -> audience.hideBossBar(bossBar), props.stay(), TimeUnit.SECONDS)
        .run();
  }

  @Override
  public InteliBossBarMessage clone() {
    return new InteliBossBarMessage(props.toBuilder().build(), text);
  }

  @Override
  public InteliBossBarMessage replace(Replacer replacer) {
    this.text = ComponentUtil.colorizeFromBukkit(replacer.accept(ComponentUtil.contentFromComponent(text)));
    return this;
  }
}
