package com.oop.inteliframework.message.title;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = true)
@ToString
public class TitleProps {

    @Builder.Default()
    private final long fadeIn = 10;

    @Builder.Default()
    private final long stay = 10;

    @Builder.Default()
    private final long fadeOut = 10;

}
