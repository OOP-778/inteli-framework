package com.oop.inteliframework.commons.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InteliTriPair<T, S, C> {
    private T first;
    private S second;
    private C third;
}
