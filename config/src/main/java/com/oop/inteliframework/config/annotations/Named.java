package com.oop.inteliframework.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.NonNull;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Named {

    /**
     * Defines the path of the target in configuration
     *
     * @return the path of the target
     */
    @NonNull
    String value();
}
