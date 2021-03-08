package com.oop.inteliframework.command;

public class ExecutorWrapper {
    public <T extends ExecutorWrapper> T as(Class<T> type) {
        return (T) this;
    }
}
