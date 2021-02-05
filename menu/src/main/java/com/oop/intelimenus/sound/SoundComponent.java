package com.oop.intelimenus.sound;

import com.oop.intelimenus.component.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SoundComponent implements Component<SoundComponent> {

    private final Map<String, WrappedSound> soundMap = new HashMap<>();

    public Optional<WrappedSound> get(String name) {
        return Optional.ofNullable(soundMap.get(name.toLowerCase()));
    }

    public void put(String name, WrappedSound wrappedSound) {
        soundMap.put(name.toLowerCase(), wrappedSound);
    }

    @Override
    public SoundComponent clone() {
        SoundComponent component = new SoundComponent();
        component.soundMap.putAll(soundMap);
        return component;
    }
}
