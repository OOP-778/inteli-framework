package com.oop.inteliframework.message.title;

import com.oop.inteliframework.message.api.InteliMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.title.Title;

import java.time.Duration;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
@ToString
@Setter
public class InteliTitleMessage implements InteliMessage<InteliTitleMessage> {

    private final TitleProps props;
    private Component title;
    private Component subtitle;

    @Override
    public InteliTitleMessage replace(Consumer<TextReplacementConfig.Builder> builderConsumer) {
        return new InteliTitleMessage(
                props,
                title.replaceText(builderConsumer),
                subtitle.replaceText(builderConsumer)
        );
    }

    @Override
    public Component toComponent() {
        return Component.empty()
                .append(title)
                .append(subtitle);
    }

    @Override
    public void send(Audience audience) {
        Title title = Title.title(
                this.title,
                subtitle,
                Title.Times.of(
                        Duration.ofMillis(props.fadeIn()),
                        Duration.ofMillis(props.stay()),
                        Duration.ofMillis(props.fadeOut())
                )
        );
        audience.showTitle(title);
    }
}
