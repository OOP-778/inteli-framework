import com.oop.inteliframework.config.annotations.Named;
import com.oop.inteliframework.config.annotations.NodeKey;
import com.oop.inteliframework.config.property.PrimitiveProperty;

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
