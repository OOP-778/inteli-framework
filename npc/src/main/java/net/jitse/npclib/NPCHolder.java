package net.jitse.npclib;

import com.oop.inteliframework.commons.util.InteliPair;
import lombok.Getter;
import net.jitse.npclib.internal.NPCBase;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** This class holds every loaded NPC */
public class NPCHolder {
  @Getter private final AtomicInteger size = new AtomicInteger(0);

  // This holds all NPC as small bits, that way we can get info faster without the need of looping
  // thru every NPC
  // This is the most performant way
  // Using map inside map is bad, but eh
  private final Map<String, Map<InteliPair<Integer, Integer>, Map<UUID, NPCBase>>> storedNPCS =
      new HashMap<>();

  public void add(NPCBase npc) {
    final NPCBase put = chunkNPCS(npc.getLocation()).put(npc.getUniqueId(), npc);
    if (put == null) size.incrementAndGet();
  }

  public void remove(NPCBase npc) {
    if (chunkNPCS(npc.getLocation()).remove(npc.getUniqueId()) != null) size.decrementAndGet();
  }

  public Map<InteliPair<Integer, Integer>, Map<UUID, NPCBase>> worldNPCS(String world) {
    return storedNPCS.computeIfAbsent(world, $ -> new ConcurrentHashMap<>());
  }

  public Map<UUID, NPCBase> chunkNPCS(String world, int chunkX, int chunkZ) {
    return worldNPCS(world)
        .computeIfAbsent(new InteliPair<>(chunkX, chunkZ), $ -> new ConcurrentHashMap<>());
  }

  public Map<UUID, NPCBase> chunkNPCS(Location location) {
    return chunkNPCS(
        location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
  }
}
