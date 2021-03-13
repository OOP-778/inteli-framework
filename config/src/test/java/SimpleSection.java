import com.oop.inteliframework.config.Configurable;
import com.oop.inteliframework.config.annotations.Comment;
import com.oop.inteliframework.config.annotations.Named;
import com.oop.inteliframework.config.annotations.NodeKey;
import com.oop.inteliframework.config.property.PrimitiveProperty;

@Comment({"Hello! I'm gay"})
public class SimpleSection implements Configurable {

  @NodeKey()
  private final PrimitiveProperty.Mutable<String> name = PrimitiveProperty.Mutable.fromString("Hello!");

  @Named("item")
  private final PrimitiveProperty.Mutable<String> smth = PrimitiveProperty.Mutable.fromString("Hell1");

  public SimpleSection() {}

  public SimpleSection(String name) {
    this.name.set(name);
  }
}
