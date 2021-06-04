/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib.internal;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.event.Events;
import com.oop.inteliframework.plugin.InteliPlatform;
import net.jitse.npclib.InteliNPCModule;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCHideEvent;
import net.jitse.npclib.api.events.NPCShowEvent;
import net.jitse.npclib.api.skin.Skin;
import net.jitse.npclib.api.state.NPCAnimation;
import net.jitse.npclib.api.state.NPCSlot;
import net.jitse.npclib.api.state.NPCState;
import net.jitse.npclib.player.PlayerTracker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public abstract class NPCBase implements NPC, NPCPacketHandler {

  protected final int entityId;
  protected final Set<UUID> hasTeamRegistered = new HashSet<>();
  protected final Set<NPCState> activeStates = EnumSet.noneOf(NPCState.class);
  protected final Map<NPCSlot, ItemStack> items = new EnumMap<>(NPCSlot.class);
  private final Set<UUID> shown = new HashSet<>();
  private final Set<UUID> autoHidden = new HashSet<>();
  protected double cosFOV = Math.cos(Math.toRadians(60));
  protected UUID uuid = new UUID(new Random().nextLong(), 0);
  protected String name = uuid.toString().replace("-", "").substring(0, 10);
  protected GameProfile gameProfile = new GameProfile(uuid, name);
  protected boolean created = false;

  protected Location location;
  protected Skin skin;

  public NPCBase() {
    entityId =
        Integer.MAX_VALUE
            - InteliPlatform.getInstance()
                .safeModuleByClass(InteliNPCModule.class)
                .getNpcHolder()
                .getSize()
                .get();
  }

  @Override
  public UUID getUniqueId() {
    return uuid;
  }

  @Override
  public String getId() {
    return name;
  }

  @Override
  public NPC setSkin(Skin skin) {
    this.skin = skin;

    gameProfile.getProperties().get("textures").clear();
    if (skin != null)
      gameProfile
          .getProperties()
          .put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

    return this;
  }

  @Override
  public void destroy() {
    InteliPlatform.getInstance()
        .safeModuleByClass(InteliNPCModule.class)
        .getNpcHolder()
        .remove(this);

    // Destroy NPC for every player that is still seeing it.
    for (UUID uuid : shown) {
      if (autoHidden.contains(uuid)) continue;
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) hide(player, true); // which will also destroy the holograms
    }
  }

  public void disableFOV() {
    this.cosFOV = 0;
  }

  public void setFOV(double fov) {
    this.cosFOV = Math.cos(Math.toRadians(fov));
  }

  public Set<UUID> getShown() {
    return shown;
  }

  public Set<UUID> getAutoHidden() {
    return autoHidden;
  }

  @Override
  public Location getLocation() {
    return location;
  }

  @Override
  public World getWorld() {
    return location != null ? location.getWorld() : null;
  }

  public int getEntityId() {
    return entityId;
  }

  @Override
  public boolean isShown(Player player) {
    Objects.requireNonNull(player, "Player object cannot be null");
    return shown.contains(player.getUniqueId()) && !autoHidden.contains(player.getUniqueId());
  }

  @Override
  public NPC setLocation(Location location) {
    this.location = location;
    return this;
  }

  @Override
  public NPC create() {
    createPackets();
    this.created = true;
    return this;
  }

  @Override
  public boolean isCreated() {
    return created;
  }

  public void onLogout(Player player) {
    getAutoHidden().remove(player.getUniqueId());
    getShown().remove(player.getUniqueId());
    hasTeamRegistered.remove(player.getUniqueId());
  }

  public boolean inRangeOf(Player player) {
    if (player == null) return false;
    if (!location.getWorld().getName().equals(player.getWorld().getName())) return false;

    final PlayerTracker tracker =
        InteliPlatform.getInstance().safeModuleByClass(InteliNPCModule.class).trackerFor(player);
    final InteliPair<Integer, Integer> npcIntPairChunk = getIntPairChunk();

    return tracker.getSeenChunks().contains(npcIntPairChunk);
  }

  public boolean inViewOf(Player player) {
    Vector dir = location.toVector().subtract(player.getEyeLocation().toVector()).normalize();
    return dir.dot(player.getEyeLocation().getDirection()) >= cosFOV;
  }

  @Override
  public void show(Player player) {
    show(player, false);
  }

  public void show(Player player, boolean auto) {
    NPCShowEvent event = new NPCShowEvent(this, player, auto);
    Events.call(event);

    if (event.isCancelled()) {
      return;
    }

    if (isShown(player)) {
      return;
    }

    if (auto) {
      sendShowPackets(player);
      sendMetadataPacket(player);
      sendEquipmentPackets(player);

      // NPC is auto-shown now, we can remove the UUID from the set.
      autoHidden.remove(player.getUniqueId());
      return;
    }

    // Adding the UUID to the set.
    shown.add(player.getUniqueId());

    if (inRangeOf(player) && inViewOf(player)) {
      // The player can see the NPC and is in range, send the packets.
      sendShowPackets(player);
      sendMetadataPacket(player);
      sendEquipmentPackets(player);
    } else {
      // We'll wait until we can show the NPC to the player via auto-show.
      autoHidden.add(player.getUniqueId());
    }
  }

  @Override
  public void hide(Player player) {
    hide(player, false);
  }

  public void hide(Player player, boolean auto) {
    NPCHideEvent event = new NPCHideEvent(this, player, auto);
    Events.call(event);

    if (event.isCancelled()) {
      return;
    }

    if (!shown.contains(player.getUniqueId())) {
      throw new IllegalArgumentException(
          "NPC cannot be hidden from player before calling NPC#show first");
    }

    if (auto) {
      if (autoHidden.contains(player.getUniqueId())) {
        throw new IllegalStateException("NPC cannot be auto-hidden twice");
      }

      sendHidePackets(player);

      // NPC is auto-hidden now, we will add the UUID to the set.
      autoHidden.add(player.getUniqueId());
      return;
    }

    // Removing the UUID from the set.
    shown.remove(player.getUniqueId());

    if (inRangeOf(player)) {
      // The player is in range of the NPC, send the packets.
      sendHidePackets(player);
    } else {
      // We don't have to send any packets, just don't let it auto-show again by removing the UUID
      // from the set.
      autoHidden.remove(player.getUniqueId());
    }
  }

  @Override
  public boolean getState(NPCState state) {
    return activeStates.contains(state);
  }

  @Override
  public NPC toggleState(NPCState state) {
    if (activeStates.contains(state)) {
      activeStates.remove(state);
    } else {
      activeStates.add(state);
    }

    // Send a new metadata packet to all players that can see the NPC.
    for (UUID shownUuid : shown) {
      Player player = Bukkit.getPlayer(shownUuid);
      if (player != null && isShown(player)) {
        sendMetadataPacket(player);
      }
    }
    return this;
  }

  @Override
  public void playAnimation(NPCAnimation animation) {
    for (UUID shownUuid : shown) {
      Player player = Bukkit.getPlayer(shownUuid);
      if (player != null && isShown(player)) {
        sendAnimationPacket(player, animation);
      }
    }
  }

  @Override
  public ItemStack getItem(NPCSlot slot) {
    Objects.requireNonNull(slot, "Slot cannot be null");
    return items.get(slot);
  }

  @Override
  public NPC setItem(NPCSlot slot, ItemStack item) {
    Objects.requireNonNull(slot, "Slot cannot be null");
    items.put(slot, item);

    for (UUID shownUuid : shown) {
      Player player = Bukkit.getPlayer(shownUuid);
      if (player != null && isShown(player)) {
        sendEquipmentPacket(player, slot, false);
      }
    }
    return this;
  }

  @Override
  public void lookAt(Location location) {
    sendHeadRotationPackets(location);
  }

  public void qualify(Player player) {
    // If player already can see the NPC return
    if (shown.contains(player.getUniqueId())) return;

    final PlayerTracker tracker =
        InteliPlatform.getInstance().safeModuleByClass(InteliNPCModule.class).trackerFor(player);

    if (!inRangeOf(player)) return;

    shown.add(player.getUniqueId());
    tracker.getShownNPCS().add(this);
    show(player);
  }

  @Override
  public InteliPair<Integer, Integer> getIntPairChunk() {
    return new InteliPair<>(location.getBlockX() >> 4, location.getBlockZ() >> 4);
  }
}
