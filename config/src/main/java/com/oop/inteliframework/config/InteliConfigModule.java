package com.oop.inteliframework.config;

import com.oop.inteliframework.config.property.custom.PropertyHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InteliConfigModule {

    // This map stores custom object handlers
    public static final Map<Class, PropertyHandler> propertyHandlerMap = new ConcurrentHashMap<>();

    // Stores all validated classes to not redo the validation
    public static final Set<Class> validatedClasses = new HashSet<>();
}
