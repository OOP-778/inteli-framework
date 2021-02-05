package com.oop.intelimenus.sound;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WrappedSound {

    private final String name;
    private final float volume;
    private final float pitch;
    private final float yaw;
}
