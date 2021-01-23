package com.oop.inteliframework.hologram.nms.V1_8_R2;

import net.minecraft.server.v1_8_R2.DataWatcher;
import net.minecraft.server.v1_8_R2.Entity;
import net.minecraft.server.v1_8_R2.EntityItem;
import net.minecraft.server.v1_8_R2.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class DataWatcherHelper {
    private List<DataWatcher.WatchableObject> watchableObjects = new LinkedList<>();
    private Entity entity;

    public DataWatcherHelper(Entity entity) {
        this.entity = entity;

        for (DataWatcher.WatchableObject watchableObject : entity.getDataWatcher().c()) {
            if (watchableObject.a() == 10 || watchableObject.a() == 2)
                watchableObjects.add(new DataWatcher.WatchableObject(watchableObject.c(), watchableObject.a(), watchableObject.b()));
            else
                watchableObjects.add(watchableObject);
        }
    }

    public void changeDisplayName(String displayName) {
        if (entity instanceof EntityItem) return;
        watchableObjects.stream()
                .filter(object -> object.a() == 2)
                .findFirst()
                .ifPresent(object -> object.a(displayName));
    }

    public ItemStack getItemStack() {
        if (!(entity instanceof EntityItem)) return null;
        return watchableObjects.stream()
                .filter(object -> object.a() == 10)
                .findFirst()
                .map(ob -> (ItemStack) ob.b())
                .orElse(null);
    }

    public void setItemStack(ItemStack itemStack) {
        if (!(entity instanceof EntityItem)) return;

        watchableObjects.stream()
                .filter(object -> object.a() == 10)
                .findFirst()
                .ifPresent(object -> object.a(itemStack.cloneItemStack()));
    }

    public List<DataWatcher.WatchableObject> getWatchableObjects() {
        return watchableObjects;
    }
}
