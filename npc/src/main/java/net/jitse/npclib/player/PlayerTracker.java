package net.jitse.npclib.player;

import com.oop.inteliframework.commons.util.InteliPair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jitse.npclib.internal.NPCBase;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class tracks the npcs that player can see This is used for faster detection when player
 * interacts and updates once moved
 */
@RequiredArgsConstructor
public class PlayerTracker {
  private final Player player;
  private final int viewDistance;
  @Getter private final Set<NPCBase> shownNPCS = ConcurrentHashMap.newKeySet();
  private InteliPair<InteliPair<Integer, Integer>, List<InteliPair<Integer, Integer>>> lastChunk =
      null;

  public List<InteliPair<Integer, Integer>> getSeenChunks() {
    Location location = player.getLocation();
    int chunkX = location.getBlockX() >> 4;
    int chunkZ = location.getBlockZ() >> 4;

    if (lastChunk != null
        && lastChunk.getKey().getKey() == chunkX
        && lastChunk.getKey().getValue() == chunkZ) {
      return lastChunk.getValue();
    }

    List<InteliPair<Integer, Integer>> tempList = new ArrayList<>();

    // Generate offsets for the chunks
    for (int x = chunkX - viewDistance; x <= chunkX + viewDistance; x++)
      for (int z = chunkZ - viewDistance; z <= chunkZ + viewDistance; z++)
        tempList.add(new InteliPair<>(x, z));

    lastChunk = new InteliPair<>(new InteliPair<>(chunkX, chunkZ), tempList);
    return tempList;
  }
}
