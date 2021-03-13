package com.oop.inteliframework.plugin.logger;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class LoggerStyle {
    private final String errorFormat;
    private final String infoFormat;
    private final String debugFormat;

    public static LoggerStyle defaultStyle() {
        return builder()
                .debugFormat("[%name%]: %message%")
                .errorFormat("[%name%]: %message%")
                .infoFormat("[%name%]: %message%")
                .build();
    }
}
