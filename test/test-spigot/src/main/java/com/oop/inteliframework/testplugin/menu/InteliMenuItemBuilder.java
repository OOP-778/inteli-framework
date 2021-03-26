package com.oop.inteliframework.testplugin.menu;

import com.oop.inteliframework.item.type.item.InteliItem;
import com.oop.inteliframework.menu.interfaces.MenuItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class InteliMenuItemBuilder implements MenuItemBuilder {

    private final InteliItem inteliItem;
    public InteliMenuItemBuilder(ItemStack itemStack) {
        this.inteliItem = new InteliItem(itemStack);
    }

    @Override
    public ItemStack getItem() {
        return inteliItem.asBukkitStack();
    }

    @Override
    public MenuItemBuilder replace(String what, Object to) {
        return this;
    }

    @Override
    public List<String> getLore() {
        return inteliItem.provideWithMeta(meta -> meta.lore().lore());
    }

    @Override
    public MenuItemBuilder appendLore(String line) {
        inteliItem.applyMeta(meta -> meta.applyLore(lore -> lore.append(line)));
        return this;
    }

    @Override
    public MenuItemBuilder displayName(String newDisplayName) {
        inteliItem.applyMeta(meta -> meta.name(newDisplayName));
        return this;
    }

    @Override
    public MenuItemBuilder clone() {
        return new InteliMenuItemBuilder(inteliItem.asBukkitStack());
    }

    @Override
    public MenuItemBuilder appendLore(Collection lines) {
        return this;
    }

    @Override
    public MenuItemBuilder lore(List newLore) {
        return this;
    }

    @Override
    public MenuItemBuilder replace(Function parser) {
        return this;
    }
}
