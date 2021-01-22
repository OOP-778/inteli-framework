import com.oop.inteliframework.config.annotations.Comment;
import com.oop.inteliframework.config.annotations.Named;
import com.oop.inteliframework.config.property.Property;

@Comment({"Test Comment"})
@Named("test")
public class TestConfig {

    @Comment("Test Property Comment")
    @Named("property")
    private Property<Integer> testProperty = Property.create(2);

}
