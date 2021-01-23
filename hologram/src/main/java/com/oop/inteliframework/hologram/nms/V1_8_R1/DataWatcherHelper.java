package com.oop.inteliframework.hologram.nms.V1_8_R1;

import net.minecraft.server.v1_8_R1.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DataWatcherHelper {
    private List<WatchableObject> watchableObjects = new LinkedList<>();
    private Entity entity;

    public DataWatcherHelper(Entity entity) {
        this.entity = entity;

        for (WatchableObject watchableObject : (ArrayList<WatchableObject>) entity.getDataWatcher().c()) {
            if (watchableObject.a() == 10 || watchableObject.a() == 2)
                watchableObjects.add(new WatchableObject(watchableObject.c(), watchableObject.a(), watchableObject.b()));
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

    public List<WatchableObject> getWatchableObjects() {
        return watchableObjects;
    }
}
