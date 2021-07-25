package com.oop.inteliframework.event.bungee;

import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;

public abstract class BungeeEventHandler<T extends Event> implements Listener {

    public abstract void handle(T event);

}
