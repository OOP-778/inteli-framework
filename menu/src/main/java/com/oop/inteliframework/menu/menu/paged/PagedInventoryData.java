package com.oop.inteliframework.menu.menu.paged;

import com.oop.inteliframework.menu.menu.simple.InventoryData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

public class PagedInventoryData<T> extends InventoryData {

  @Setter(AccessLevel.PROTECTED)
  @Getter
  private T[] currentData;

  @Setter(AccessLevel.PROTECTED)
  @Getter
  private int pages;

  public PagedInventoryData(InteliPagedMenu<T> menu, int size, Inventory inventory) {
    super(menu, size, inventory);
  }

  @Override
  public String toString() {
    return super.toString()
        + " ~ IPagedInventoryData{"
        + "currentData=size{"
        + currentData.length
        + "}"
        + ", pages="
        + pages
        + '}';
  }
}
