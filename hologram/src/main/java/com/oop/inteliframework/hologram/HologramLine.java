package com.oop.inteliframework.hologram;

import com.oop.inteliframework.hologram.nms.WrappedArmorStand;
import com.oop.inteliframework.hologram.util.UpdateableObject;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public abstract class HologramLine<T, V> {
    protected WrappedArmorStand wrappedArmorStand;
    protected UpdateableObject<Location> location = new UpdateableObject<>(null);
    protected HologramView hologramView;

    public T setLocation(Location location) {
        if (this.location.get() != null && this.location.get().equals(location)) return (T) this;
        this.location.set(location);
        return (T) this;
    }

    protected void setHologramView(HologramView hologramView) {
        this.hologramView = hologramView;
    }

    public synchronized void preUpdate() {
        // Check if armor stand exists
        if (wrappedArmorStand == null) {
            wrappedArmorStand = WrappedArmorStand.supplier.apply(location.get(), this);
            wrappedArmorStand.setGravity(false);
            wrappedArmorStand.setVisible(false);
            wrappedArmorStand.setCustomNameVisibility(true);

            armorStandCreate();
        }
    }

    public synchronized void update() {
    }

    public synchronized void postUpdate() {
        // Update location
        if (location.isUpdated()) {

            wrappedArmorStand.setLocation(location.get());
            wrappedArmorStand.outputLocation();
        }

        wrappedArmorStand.update();
    }

    public synchronized void remove() {
        if (wrappedArmorStand != null)
            wrappedArmorStand.remove(getHologramView().getViewers().toArray(new Player[0]));
    }

    protected void handleRemove(Player player) {
        wrappedArmorStand.remove(player);
    }

    protected void handleAdd(Player player) {
        wrappedArmorStand.spawn(player);
    }

    protected void armorStandCreate() {
    }

    public abstract void clearData();
}
