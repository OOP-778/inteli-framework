import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.config.Configurable;
import com.oop.inteliframework.config.annotations.Comment;
import com.oop.inteliframework.config.annotations.Named;
import com.oop.inteliframework.config.property.CollectionProperty;
import com.oop.inteliframework.config.property.MapProperty;
import com.oop.inteliframework.config.property.PrimitiveProperty;
import com.oop.inteliframework.config.property.custom.ObjectProperty;

import java.util.*;

@Comment(
    value = {"Test Comment"},
    override = true)
public class TestConfig implements Configurable {

  public PrimitiveProperty.Mutable<Integer> getTestValue() {
    return testValue;
  }

  public CollectionProperty<Integer, List<Integer>> getTestList() {
    return testList;
  }

  public MapProperty<String, String, Map<String, String>> getTestMap() {
    return testMap;
  }

  public ObjectProperty.Mutable<SimpleSection> getSimpleSection() {
    return simpleSection;
  }

  @Comment("Test Property Comment")
  @Named("testValue")
  private final PrimitiveProperty.Mutable<Integer> testValue =
      PrimitiveProperty.Mutable.emptyInt().set(1);

  @Named("testList")
  private final CollectionProperty<Integer, List<Integer>> testList =
      CollectionProperty.from(new ArrayList<>(), int.class, 1, 2, 2, 4);

  @Named("testMap")
  private final MapProperty<String, String, Map<String, String>> testMap =
      MapProperty.from(
          new HashMap<>(), String.class, String.class, new InteliPair<>("key", "value"));

  private final ObjectProperty.Mutable<SimpleSection> simpleSection =
      ObjectProperty.Mutable.from(new SimpleSection());

  @Named("Hey")
  private final ObjectProperty.Mutable<UUID> userUUid = ObjectProperty.Mutable.from(UUID.randomUUID());
}
