package com.oop.inteliframework.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for defining associated config
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Config {
    /**
     * Import from resources
     *
     * @return if it should import from the resources if not found
     */
    boolean importFromResources() default false;
}
