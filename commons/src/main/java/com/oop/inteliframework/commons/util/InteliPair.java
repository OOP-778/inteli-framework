package com.oop.inteliframework.commons.util;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
public class InteliPair<K, V> {

    private K key;
    private V value;

}
