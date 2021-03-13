import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.ValueNode;
import com.oop.inteliframework.config.property.custom.PropertyHandler;

import java.util.UUID;

public class UUIDHandler implements PropertyHandler<UUID> {
    @Override
    public Node toNode(String underName, UUID object) {
        return new ValueNode(underName, object);
    }

    @Override
    public UUID fromNode(Node node) {
        return UUID.randomUUID();
    }
}
