package com.oop.inteliframework.menu.menu.simple;

import com.google.common.base.Preconditions;
import com.oop.inteliframework.menu.attribute.AttributeComponent;
import com.oop.inteliframework.menu.attribute.Attributes;
import com.oop.inteliframework.menu.button.IButton;
import com.oop.inteliframework.menu.designer.MenuDesigner;
import com.oop.inteliframework.menu.menu.paged.InteliPagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class MenuBuilder<T extends InteliMenu, B extends MenuBuilder<T, B>> {

  private final T menu;

  public MenuBuilder(T menu) {
    this.menu = menu;
  }

  public static PlayerPasser<SimpleMenuBuilder<InteliMenu>> simpleMenu() {
    return new PlayerPasser<>(
        player -> new SimpleMenuBuilder<>(new InteliMenu(player, 4, "&cDefault Title")));
  }

  public static <O> PlayerPasser<PagedMenuBuilder<InteliPagedMenu<O>, O>> pagedMenu(
      Class<O> objectClass) {
    return new PlayerPasser<>(
        player -> new PagedMenuBuilder<>(new InteliPagedMenu<>(player, 4, "&cDefault Title")));
  }

  public static <T extends InteliMenu> PlayerPasser<SimpleMenuBuilder<T>> customSimpleMenu(
      Function<Player, T> constructor) {
    return new PlayerPasser<>(player -> new SimpleMenuBuilder<>(constructor.apply(player)));
  }

  public static <T extends InteliPagedMenu<O>, O>
      PlayerPasser<PagedMenuBuilder<T, O>> customPagedMenu(
          Class<O> clazz, Function<Player, T> constructor) {
    return new PlayerPasser<>(player -> new PagedMenuBuilder<>(constructor.apply(player)));
  }

  public T menu() {
    return menu;
  }

  public B apply(Consumer<T> consumer) {
    consumer.accept(menu);
    return (B) this;
  }

  public B rows(int rows) {
    Preconditions.checkArgument(rows < 7, "The maximum size of an inventory is 54");
    menu.setSize(rows * 9);
    return (B) this;
  }

  public B size(int size) {
    Preconditions.checkArgument(
        size < 55 && size % 9 == 0,
        "The maximum size of an inventory is 54 or the size is not dividable by 9");
    menu.setSize(size);
    return (B) this;
  }

  public B title(Function<T, String> titleSupplier) {
    menu.setTitleSupplier((Function<InteliMenu, String>) titleSupplier);
    return (B) this;
  }

  public B title(Supplier<String> titleSupplier) {
    menu.setTitleSupplier(menu -> titleSupplier.get());
    return (B) this;
  }

  public B rebuildOnOpen() {
    apply(
        menu ->
            menu.applyComponent(
                AttributeComponent.class, comp -> comp.addAttribute(Attributes.REBUILD_ON_OPEN)));
    return (B) this;
  }

  public B title(String title) {
    menu.setTitleSupplier(menu -> title);
    return (B) this;
  }

  public MenuDesigner<T, B> designer() {
    return new MenuDesigner<>(menu, (B) this);
  }

  public static class PagedMenuBuilder<T extends InteliPagedMenu<O>, O>
      extends MenuBuilder<T, PagedMenuBuilder<T, O>> {

    public PagedMenuBuilder(T menu) {
      super(menu);
    }

    public PagedMenuBuilder<T, O> objectsProvider(Supplier<Collection<O>> objectProvider) {
      menu().setObjectsProvider(objectProvider);
      return this;
    }

    public PagedMenuBuilder<T, O> pagedButtonBuilder(Function<O, IButton> buttonFunction) {
      menu().setPagedButtonBuilder(buttonFunction);
      return this;
    }
  }

  public static class SimpleMenuBuilder<T extends InteliMenu>
      extends MenuBuilder<T, SimpleMenuBuilder<T>> {

    public SimpleMenuBuilder(T menu) {
      super(menu);
    }
  }

  @AllArgsConstructor
  public static class PlayerPasser<T> {

    private final Function<Player, T> whenPassed;

    public T who(Player player) {
      return whenPassed.apply(player);
    }
  }
}
