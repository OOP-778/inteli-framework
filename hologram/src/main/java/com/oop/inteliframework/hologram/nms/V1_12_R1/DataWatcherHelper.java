package com.oop.inteliframework.hologram.nms.V1_12_R1;

import net.minecraft.server.v1_12_R1.DataWatcher;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityItem;
import net.minecraft.server.v1_12_R1.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class DataWatcherHelper {
    private List<DataWatcher.Item> watchableObjects = new LinkedList<>();
    private Entity entity;

    public DataWatcherHelper(Entity entity) {
        this.entity = entity;
        watchableObjects.addAll(entity.getDataWatcher().c());
    }

    public void changeDisplayName(String displayName) {
        if (entity instanceof EntityItem) return;
        watchableObjects.stream()
                .filter(object -> object.a().a() == 2)
                .findFirst()
                .ifPresent(object -> object.a(displayName));
    }

    public ItemStack getItemStack() {
        if (!(entity instanceof EntityItem)) return null;
        return watchableObjects.stream()
                .filter(object -> object.a().a() == 6)
                .findFirst()
                .map(ob -> (ItemStack) ob.b())
                .orElse(null);
    }

    public void setItemStack(ItemStack itemStack) {
        if (!(entity instanceof EntityItem)) return;

        watchableObjects.stream()
                .filter(object -> object.a().a() == 6)
                .findFirst()
                .ifPresent(object -> object.a(itemStack.cloneItemStack()));
    }

    public List<DataWatcher.Item> getWatchableObjects() {
        return watchableObjects;
    }
}
