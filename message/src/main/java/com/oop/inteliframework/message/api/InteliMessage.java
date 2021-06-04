package com.oop.inteliframework.message.api;

/** Base message */
public interface InteliMessage<T extends InteliMessage>
    extends Replaceable<T>, Sendable, Componentable {

    T clone();

}
