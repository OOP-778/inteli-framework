package com.oop.inteliframework.commons.util;

public class Preconditions {
    /**
     * Check an argument
     * @param check a boolean which determines if the check succeed
     * @param message error message that will be thrown if check failed
     */
    public static void checkArgument(boolean check, String message) {
        if (!check)
            throw new IllegalStateException(message);
    }
}
