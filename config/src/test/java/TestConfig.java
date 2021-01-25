import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.config.annotations.Comment;
import com.oop.inteliframework.config.annotations.Config;
import com.oop.inteliframework.config.annotations.Named;
import com.oop.inteliframework.config.property.CollectionProperty;
import com.oop.inteliframework.config.property.MapProperty;
import com.oop.inteliframework.config.property.PrimitiveProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Comment({"Test Comment"})
@Named("test.yml")
@Config(importFromResources = true)
public class TestConfig {

    @Comment("Test Property Comment")
    @Named("testValue")
    private final PrimitiveProperty.Mutable<Integer> testValue = PrimitiveProperty.Mutable
            .emptyInt()
            .set(1);

    @Named("testList")
    private final CollectionProperty<Integer, List<Integer>> testList = CollectionProperty
            .from(new ArrayList<>(), int.class, 1, 2, 2, 4);

    @Named("testMap")
    private final MapProperty<String, String, Map> testMap = MapProperty
            .from(new HashMap<>(), String.class, String.class, new InteliPair<>("key", "value"));
}
