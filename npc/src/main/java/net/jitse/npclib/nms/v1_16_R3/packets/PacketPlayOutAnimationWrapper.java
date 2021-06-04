package net.jitse.npclib.nms.v1_16_R3.packets;

import com.oop.inteliframework.commons.util.SimpleReflection;
import lombok.SneakyThrows;
import net.jitse.npclib.api.state.NPCAnimation;
import net.minecraft.server.v1_16_R3.PacketPlayOutAnimation;

public class PacketPlayOutAnimationWrapper {

  @SneakyThrows
  public PacketPlayOutAnimation create(NPCAnimation npcAnimation, int entityId) {
    PacketPlayOutAnimation packetPlayOutAnimation = new PacketPlayOutAnimation();

    SimpleReflection.getField(packetPlayOutAnimation.getClass(), "a")
        .set(packetPlayOutAnimation, entityId);
    SimpleReflection.getField(packetPlayOutAnimation.getClass(), "b")
        .set(packetPlayOutAnimation, npcAnimation.getId());

    return packetPlayOutAnimation;
  }
}
