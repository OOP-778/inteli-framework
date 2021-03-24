package com.oop.inteliframework.message;

import com.oop.inteliframework.api.InteliMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.function.Consumer;

public class InteliActionBarMessage implements InteliMessage<InteliActionBarMessage> {

    @Override
    public InteliActionBarMessage replace(Consumer<TextReplacementConfig.Builder> builderConsumer) {
        return null;
    }

    @Override
    public Component toComponent() {
        return null;
    }

    @Override
    public void send(Audience audience) {
    }
}
