package com.oop.inteliframework.commons.util;

import java.util.LinkedList;

public class InsertableList<T> extends LinkedList<T> {
    public int insert(int index, InsertMethod method, T ...what) {
        switch (method) {
            case AFTER:
                for (T object : what)
                    add(index += 1, object);
                break;

            case BEFORE:
                for (T object : what)
                    add(index -= 1, object);
                break;

            case REPLACE:
                boolean replaced = false;
                for (T object : what) {
                    if (!replaced) {
                        replaced = true;
                        set(index, object);
                        continue;
                    }
                    add(index += 1, object);
                }
        }
        return index;
    }

    public int insert(int index, T ...what) {
        return insert(index, InsertMethod.AFTER, what);
    }

    public int insert(T where, InsertMethod method, T ...what) {
        return insert(indexOf(where), method, what);
    }

    public static enum InsertMethod {
        AFTER,
        BEFORE,
        REPLACE
    }
}
