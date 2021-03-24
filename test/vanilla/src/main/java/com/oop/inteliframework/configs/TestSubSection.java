package com.oop.inteliframework.configs;

import com.oop.inteliframework.config.property.annotations.Named;
import com.oop.inteliframework.config.property.annotations.NodeKey;
import com.oop.inteliframework.config.property.property.PrimitiveProperty;

public class TestSubSection {
    @NodeKey
    private final PrimitiveProperty.Mutable<String> name =
        PrimitiveProperty.Mutable
            .fromString("SectionName");

    @Named("gay")
    private final PrimitiveProperty.Mutable<String> gay = PrimitiveProperty
        .Mutable
        .fromString("GAY");
}
