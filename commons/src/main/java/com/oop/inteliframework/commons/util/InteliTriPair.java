package com.oop.inteliframework.commons.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Data
public class InteliTriPair<T, S, C> {
    private T first;
    private S second;
    private C third;
}
