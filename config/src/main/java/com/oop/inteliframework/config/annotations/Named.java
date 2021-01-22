package com.oop.inteliframework.config.annotations;

import lombok.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Named {

    /**
     * Defines the name of the target in configuration
     *
     * @return the name of the target
     */
    @NonNull
    String value();
}
