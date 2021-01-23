package com.oop.inteliframework.hologram.nms.V1_15_R1;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DataWatcherHelper {
    private List<DataWatcher.Item> watchableObjects = new LinkedList<>();
    private Entity entity;

    public DataWatcherHelper(Entity entity) {
        this.entity = entity;
        watchableObjects.addAll(entity.getDataWatcher().c());
    }

    public void changeDisplayName(String displayName) {
        if (entity instanceof EntityItem) return;

        entity.setInvisible(true);

        watchableObjects.stream()
                .filter(object -> object.a().a() == 2)
                .findFirst()
                .ifPresent(object ->
                    object.a(Optional.of(CraftChatMessage.fromString(displayName)[0]))
                );
    }

    public ItemStack getItemStack() {
        if (!(entity instanceof EntityItem)) return null;
        return watchableObjects.stream()
                .filter(object -> object.a().a() == 7)
                .findFirst()
                .map(ob -> (ItemStack) ob.b())
                .orElse(null);
    }

    public void setItemStack(ItemStack itemStack) {
        if (!(entity instanceof EntityItem)) return;
        watchableObjects.stream()
                .filter(object -> object.a().a() == 7)
                .findFirst()
                .ifPresent(object -> object.a(itemStack.cloneItemStack()));
    }

    public List<DataWatcher.Item> getWatchableObjects() {
        return watchableObjects;
    }
}
