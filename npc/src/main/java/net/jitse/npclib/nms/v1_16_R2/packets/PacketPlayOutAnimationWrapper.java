package net.jitse.npclib.nms.v1_16_R2.packets;

import net.jitse.npclib.api.state.NPCAnimation;
import net.minecraft.server.v1_16_R2.PacketPlayOutAnimation;

public class PacketPlayOutAnimationWrapper {

  public PacketPlayOutAnimation create(NPCAnimation npcAnimation, int entityId) {
    PacketPlayOutAnimation packetPlayOutAnimation = new PacketPlayOutAnimation();

    SimpleReflection.getField(packetPlayOutAnimation.getClass(), "a", int.class)
        .set(packetPlayOutAnimation, entityId);
    SimpleReflection.getField(packetPlayOutAnimation.getClass(), "b", int.class)
        .set(packetPlayOutAnimation, npcAnimation.getId());

    return packetPlayOutAnimation;
  }
}