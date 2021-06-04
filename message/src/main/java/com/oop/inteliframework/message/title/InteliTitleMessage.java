package com.oop.inteliframework.message.title;

import com.oop.inteliframework.message.ComponentUtil;
import com.oop.inteliframework.message.Replacer;
import com.oop.inteliframework.message.api.Componentable;
import com.oop.inteliframework.message.api.InteliMessage;
import com.oop.inteliframework.message.api.Sendable;
import lombok.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.time.Duration;

@Getter
@AllArgsConstructor
@ToString
@Setter
public class InteliTitleMessage
    implements InteliMessage<InteliTitleMessage>, Componentable, Sendable {

  private @NonNull final TitleProps props;
  private @NonNull Component title;
  private Component subtitle;

  @Override
  public Component toComponent() {
    return Component.empty().append(title).append(subtitle);
  }

  @Override
  public void send(Audience audience) {
    Title title =
        Title.title(
            this.title,
            subtitle,
            Title.Times.of(
                Duration.ofSeconds(props.fadeIn()),
                Duration.ofSeconds(props.stay()),
                Duration.ofSeconds(props.fadeOut())));
    audience.showTitle(title);
  }

  @Override
  public InteliTitleMessage clone() {
    return new InteliTitleMessage(props.toBuilder().build(), title, subtitle);
  }

  @Override
  public InteliTitleMessage replace(Replacer replacer) {
    this.title =
        ComponentUtil.colorizeFromBukkit(
            replacer.accept(ComponentUtil.contentFromComponent(title)));
    this.subtitle =
        ComponentUtil.colorizeFromBukkit(
            replacer.accept(ComponentUtil.contentFromComponent(subtitle)));
    return this;
  }
}
