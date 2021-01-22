package com.oop.inteliframework.hologram;

import com.oop.inteliframework.commons.util.ConcurrentObject;
import com.oop.inteliframework.commons.util.InteliClock;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.hologram.util.UpdateableObject;
import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

public class Hologram {
    // Views of the hologram
    private final ConcurrentObject<List<HologramView>> views = new ConcurrentObject<>(new LinkedList<>());
    // Update task
    @Setter(AccessLevel.PROTECTED)
    private ScheduledFuture<?> updateTask;
    // Clock for updating viewers
    private InteliClock viewersClock = new InteliClock(10);
    // View distance in chunks
    private int viewDistance = 2;

    // Global refresh rate
    @Setter
    private long refreshRate;

    // Base Location for the hologram
    private UpdateableObject<Location> location = new UpdateableObject<>(null);

    // Chunks cache for getting players in server view distance
    private InteliPair<Integer, Integer>[] chunksCache;

    public boolean containsViews() {
        return !views.use(List::isEmpty);
    }

    public void addView(HologramView view) {
        // If refresh rate is not assigned
        if (view.getRefreshRate() == -1)
            view.setRefreshRate(refreshRate);

        views.modify(list -> list.add(view));
    }

    public void removeView(HologramView view) {
        views.modify(list -> list.remove(view));
    }

    public List<HologramView> getViews() {
        return Collections.unmodifiableList(views.getObject());
    }

    public synchronized void update() {
        Location location = this.location.current();
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        if (!location.getWorld().isChunkLoaded(chunkX, chunkZ))
            return;

        if (chunksCache == null || this.location.isUpdated())
            initializeChunksCache();

        PlayerTracker.track(location.getWorld().getName(), chunksCache);
        Collection<HologramView> coll = views.use(Collections::unmodifiableCollection);

        // Update
        boolean updateViewers = viewersClock.tick() || this.location.isUpdated();
        Set<Player> playersInRadius = null;
        if (updateViewers)
            playersInRadius = PlayerTracker.request(location.getWorld().getName(), chunksCache);

        for (HologramView hologramView : coll) {
            // If the view is not assigned to here
            if (hologramView.getHologram() == null)
                hologramView.setHologram(this);

            // Location
            if (this.location.isUpdated())
                hologramView.setLocation(location);

            // Refresh
            hologramView.update();

            // Viewers
            if (updateViewers)
                hologramView.updateViewers(playersInRadius);

            // Animate
            hologramView.animate();
        }

        if (this.location.isUpdated())
            this.location.get();
    }

    private void initializeChunksCache() {
        Location location = this.location.current();
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;

        List<InteliPair<Integer, Integer>> tempList = new ArrayList<>();

        // Generate offsets for the chunks
        for (int x = chunkX - viewDistance; x <= chunkX + viewDistance; x++)
            for (int z = chunkZ - viewDistance; z <= chunkZ + viewDistance; z++)
                tempList.add(new InteliPair<>(x, z));

        this.chunksCache = tempList.toArray(new InteliPair[0]);
    }

    public void remove() {
        views.modify(list -> list.forEach(HologramView::remove));
    }

    public Location getLocation() {
        return location.current();
    }

    public void setLocation(Location location) {
        this.location.set(location);
    }
}
