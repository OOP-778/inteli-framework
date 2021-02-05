package com.oop.inteliframework.commons.util;

import java.util.*;

public class CollectionHelper {
    public static <T extends Collection<V>, V> T addAndReturn(T to, V... what) {
        to.addAll(Arrays.asList(what));
        return to;
    }

    public static <T> Collection<T> copyAndAdd(Collection<T> to, T... what) {
        List<T> copy = new ArrayList<>(to);
        copy.addAll(Arrays.asList(what));

        return copy;
    }

    public static <T> T[] joinArrays(T[] ...arrays) {
        List<T> list = new LinkedList<>();
        for (T[] array : arrays)
            list.addAll(Arrays.asList(array));

        return (T[]) list.toArray();
    }
}
