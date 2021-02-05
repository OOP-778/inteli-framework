package com.oop.inteliframework.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Comment {

    /**
     * Defines the comment for a specific value
     *
     * @return the comment that applies with certain Valuable
     */
    @NotNull
    String[] value();

    /**
     * If comments already present, should it override?
     *
     * @return if the comments should be overwritten
     */
    boolean override() default false;
}
