package com.oop.inteliframework.menu.menu.paged;

import com.google.common.base.Preconditions;
import com.oop.inteliframework.menu.attribute.AttributeComponent;
import com.oop.inteliframework.menu.attribute.Attributes;
import com.oop.inteliframework.menu.button.IButton;
import com.oop.inteliframework.menu.button.state.StateComponent;
import com.oop.inteliframework.menu.button.state.StateRequestComponent;
import com.oop.inteliframework.menu.data.DataComponent;
import com.oop.inteliframework.menu.menu.simple.InteliMenu;
import com.oop.inteliframework.menu.menu.simple.InventoryData;
import com.oop.inteliframework.menu.slot.InteliSlot;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class InteliPagedMenu<T extends Object> extends InteliMenu {

  @Setter private Supplier<Collection<T>> objectsProvider;

  @Setter private Function<T, IButton> pagedButtonBuilder;
  @Getter private int currentPage = 1;
  private int pagesLimit = -1;

  public InteliPagedMenu(Player viewer, int rows, String title) {
    super(viewer, rows, title);
    applyComponent(
        StateRequestComponent.class,
        src -> {
          src.register(
              slot ->
                  slot.getHolder().isPresent()
                      && slot.getHolder().get().getComponent(StateComponent.class).isPresent()
                      && slot.getHolder()
                          .get()
                          .getComponent(AttributeComponent.class)
                          .filter(ac -> ac.hasAttribute(Attributes.NEXT_PAGE.get()))
                          .isPresent(),
              slot -> {
                boolean pagePresent = currentPage != getInventoryData().getPages();
                return slot.getHolder()
                    .get()
                    .getComponent(StateComponent.class)
                    .get()
                    .getState(pagePresent ? "page-available" : "page-not-available")
                    .orElseThrow(
                        () ->
                            new IllegalStateException(
                                "Failed to find state for next page button for by "
                                    + (pagePresent ? "page-available" : "page-not-available")));
              });
          src.register(
              slot ->
                  slot.getHolder().isPresent()
                      && slot.getHolder().get().getComponent(StateComponent.class).isPresent()
                      && slot.getHolder()
                          .get()
                          .getComponent(AttributeComponent.class)
                          .filter(ac -> ac.hasAttribute(Attributes.PREVIOUS_PAGE.get()))
                          .isPresent(),
              slot -> {
                boolean pagePresent = currentPage != 1;
                return slot.getHolder()
                    .get()
                    .getComponent(StateComponent.class)
                    .get()
                    .getState(pagePresent ? "page-available" : "page-not-available")
                    .orElseThrow(
                        () ->
                            new IllegalStateException(
                                "Failed to find state for previous page button for by "
                                    + (pagePresent ? "page-available" : "page-not-available")));
              });
        });
  }

  public InteliPagedMenu(Player player) {
    super(player);
  }

  @Override
  public Inventory buildMenu() {
    Preconditions.checkArgument(
        objectsProvider != null, "Failed to build cause objectsProvider is not set!");
    Preconditions.checkArgument(
        pagedButtonBuilder != null, "Failed to build cause pagedButtonBuilder is not set!");

    Inventory inventory = super.buildMenu();

    // Get paged inventory data
    PagedInventoryData<T> inventoryData = getInventoryData();

    // Get objects by the page & get the pages that we will need
    InteliSlot[] freeSlots = inventoryData.getFreeSlots();
    if (freeSlots.length == 0) {
      return inventory;
    }

    Collection<T> ts = objectsProvider.get();
    int pages =
        freeSlots.length >= ts.size() ? 1 : (int) Math.ceil((float) ts.size() / freeSlots.length);

    if (pagesLimit != -1 && pages > pagesLimit) {
      pages = pagesLimit;
    }

    inventoryData.setPages(pages);

    int startIndex = currentPage == 1 ? 0 : (currentPage * freeSlots.length) - freeSlots.length;
    int endIndex = startIndex + (Math.min(freeSlots.length, ts.size()));

    inventoryData.setCurrentData(Arrays.copyOfRange((T[]) ts.toArray(), startIndex, endIndex));
    // End of getting objects

    // Start of building paged buttons
    int objectIndex = 0;
    for (InteliSlot freeSlot : freeSlots) {
      if (inventoryData.getCurrentData().length == objectIndex) {
        break;
      }

      T object = inventoryData.getCurrentData()[objectIndex];
      if (object == null) {
        break;
      }

      IButton objectButton = pagedButtonBuilder.apply(object);

      int finalObjectIndex = objectIndex;
      objectButton.applyComponent(DataComponent.class, c -> c.add("objectIndex", finalObjectIndex));

      freeSlot.setHolder(objectButton);
      inventory.setItem(
          freeSlot.getIndex(),
          objectButton.getCurrentItem().map(Supplier::get).orElse(new ItemStack(Material.BARRIER)));

      objectIndex++;
    }

    Optional<InteliSlot> optNextPageSlot =
        getInventoryData()
            .findSlot(
                slot ->
                    slot.getHolder().isPresent()
                        && slot.getHolder().get().getComponent(StateComponent.class).isPresent()
                        && slot.getHolder()
                            .get()
                            .getComponent(AttributeComponent.class)
                            .filter(ac -> ac.hasAttribute(Attributes.NEXT_PAGE.get()))
                            .isPresent());

    Optional<InteliSlot> optPreviousPageSlot =
        getInventoryData()
            .findSlot(
                slot ->
                    slot.getHolder().isPresent()
                        && slot.getHolder().get().getComponent(StateComponent.class).isPresent()
                        && slot.getHolder()
                            .get()
                            .getComponent(AttributeComponent.class)
                            .filter(ac -> ac.hasAttribute(Attributes.PREVIOUS_PAGE.get()))
                            .isPresent());

    Stream.of(optNextPageSlot, optPreviousPageSlot)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(slot -> getInventoryData().updateSlots(slot.getIndex()));

    String title = getInventoryData().getTitle();
    if (title.contains("{pages}") || title.contains("{currentPage}")) {
      getInventoryData()
          .updateTitle(
              title
                  .replace("{pages}", getInventoryData().getPages() + "")
                  .replace("{currentPage}", currentPage + ""));
    }

    // End of building paged buttons
    return getInventoryData().getInventory();
  }

  public InteliPagedMenu<T> pagesLimit(int limit) {
    this.pagesLimit = limit;
    return this;
  }

  @Override
  public PagedInventoryData<T> getInventoryData() {
    InventoryData inventoryData = super.getInventoryData();
    if (inventoryData == null) {
      return null;
    }

    if (!(inventoryData instanceof PagedInventoryData)) {
      setInventoryData(
          new PagedInventoryData<>(
              this, inventoryData.getSlots().length, inventoryData.getInventory()));
      super.getInventoryData().getDataFrom(inventoryData);
      return getInventoryData();
    }

    return (PagedInventoryData<T>) inventoryData;
  }

  public void nextPage() {
    if (currentPage == getInventoryData().getPages()) {
      return;
    }

    System.out.println("page");
    currentPage += 1;
    refreshAction();
  }

  public void previousPage() {
    if (currentPage == 1) {
      return;
    }
    currentPage -= 1;
    refreshAction();
  }

  @Override
  public String toString() {
    return super.toString()
        + " ~ IPagedMenu{"
        + ", currentPage="
        + currentPage
        + ", pagesLimit="
        + pagesLimit
        + '}';
  }
}
