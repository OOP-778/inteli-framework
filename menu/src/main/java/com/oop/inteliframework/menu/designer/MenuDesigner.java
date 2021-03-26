package com.oop.inteliframework.menu.designer;

import com.google.common.base.Preconditions;
import com.oop.inteliframework.menu.button.IButton;
import com.oop.inteliframework.menu.button.builder.IButtonBuilder;
import com.oop.inteliframework.menu.menu.simple.InteliMenu;
import com.oop.inteliframework.menu.slot.InteliSlot;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Accessors(fluent = true)
public class MenuDesigner<T extends InteliMenu, P> {

  @Getter private final T menu;

  @Getter private P parent;

  public MenuDesigner(T menu, P parent) {
    this.menu = menu;
    this.parent = parent;
  }

  public MenuDesigner(T menu) {
    this.menu = menu;
  }

  private static Map<Character, IButton> mapOfArray(Object... objects) {
    if (objects.length % 2 != 0) {
      throw new IllegalStateException(
          "Failed to convert objects to map, because the size is not even!");
    }

    Map<Character, IButton> map = new HashMap<>();

    int len = objects.length;
    int i = 0;

    boolean inside = true;
    while (inside) {
      Object key = Objects.requireNonNull(objects[i++], "Key cannot be null!");
      Object value = Objects.requireNonNull(objects[i++], "Value cannot be null!");
      if (key instanceof String) {
        key = ((String) key).toCharArray()[0];
      }

      Preconditions.checkArgument(
          key instanceof Character, "Key is not a character! (" + key + ") at" + i);
      Preconditions.checkArgument(
          value instanceof IButton, "Value is not a button! (" + value + ") at " + i);

      map.put((Character) key, (IButton) value);
      if (i == len) {
        inside = false;
      }
    }

    return map;
  }

  public MenuDesigner<T, P> button(int slot) {
    return this;
  }

  public MenuDesigner<T, P> button(int row, int column) {
    return this;
  }

  public MenuDesigner<T, P> rows(
      @NonNull String design, @NonNull Map<Character, IButton> charDesign, int... rows) {
    for (int row : rows) {
      row(row, design, charDesign);
    }

    return this;
  }

  public MenuDesigner<T, P> rows(
      int from, int to, @NonNull String design, @NonNull Map<Character, IButton> charDesign) {
    IntStream.range(from, to).forEach(row -> row(row, design, charDesign));
    return this;
  }

  public MenuDesigner<T, P> rows(
      int[] rows, @NonNull String design, @NonNull Object... charDesign) {
    Map<Character, IButton> characterIButtonMap = mapOfArray(charDesign);
    for (int row : rows) {
      row(row, design, characterIButtonMap);
    }
    return this;
  }

  public MenuDesigner<T, P> fillEmpty(Supplier<IButton> buttonSupplier) {
    for (InteliSlot slot : menu.getSlots()) {
      if (slot.getHolder().isPresent()) {
        continue;
      }

      slot.setHolder(buttonSupplier.get());
    }
    return this;
  }

  public MenuDesigner<T, P> fillRow(int row, IButton button) {
    return row(row, "X X X X X X X X X", 'X', button);
  }

  public MenuDesigner<T, P> fillRows(int[] rows, IButton button) {
    return rows(rows, "X X X X X X X X X", 'X', button);
  }

  public MenuDesigner<T, P> fillEmpty(ItemStack itemStack) {
    return fillEmpty(() -> IButtonBuilder.of(itemStack).toButton());
  }

  public MenuDesigner<T, P> row(int row, @NonNull String design, @NonNull Object... charDesign) {
    return row(row, design, mapOfArray(charDesign));
  }

  public MenuDesigner<T, P> row(
      int row, @NonNull String design, @NonNull Map<Character, IButton> charDesign) {
    design = design.replaceAll("\\s+", "");
    Preconditions.checkArgument(
        design.length() == 9,
        "Incorrect design passed. The length should be 9! (" + design.length() + "/9) = " + design);

    int slot = row == 1 ? 0 : (row * 9) - 9;
    for (char c : design.toCharArray()) {
      IButton button = charDesign.get(c);
      if (button != null) {
        menu.setSlot(slot, button.clone());
      }
      slot++;
    }

    return this;
  }

  public MenuDesigner<T, P> setAt(int row, int offset, IButton button) {
    Preconditions.checkArgument(offset < 9, "Offset cannot be over 8!");
    int slot = row == 1 ? 0 : (row * 9) - 9;

    menu.setSlot(slot + offset, button);
    return this;
  }

  public MenuDesigner<T, P> fillAllBorders(IButton button) {
    return fillBorders(button, FillBorder.values());
  }

  public MenuDesigner<T, P> fillBorders(IButton button, FillBorder... borders) {
    for (FillBorder border : borders) {
      fillBorder(border, button);
    }

    return this;
  }

  public MenuDesigner<T, P> fillBorder(@NonNull FillBorder border, @NonNull IButton button) {
    int size = menu().getRows();
    Function<Integer, int[]> slotsByRow =
        row -> {
          // If border is left or right, only single slot is required
          if (border == FillBorder.LEFT) {
            return new int[] {row == 1 ? 0 : (row * 9) - 9};
          } else if (border == FillBorder.RIGHT) {
            return new int[] {row == 1 ? 9 : (row * 9) - 1};
          } else if (border == FillBorder.TOP) {
            return IntStream.range(0, 9).toArray();
          } else if (border == FillBorder.BOTTOM) {
            return IntStream.range((row * 9) - 9, (row * 9)).toArray();
          }
          throw new IllegalStateException("Never should be reached!");
        };

    // If borders are left or right we have to loop thru all rows
    if (border == FillBorder.LEFT || border == FillBorder.RIGHT) {
      for (int row = 1; row < size + 1; row++) {
        for (int i : slotsByRow.apply(row)) {
          menu.setSlot(i, button);
        }
      }
    } else if (border == FillBorder.TOP) {
      for (int i : slotsByRow.apply(1)) {
        menu.setSlot(i, button);
      }
    } else if (border == FillBorder.BOTTOM) {
      for (int i : slotsByRow.apply(size)) {
        menu.setSlot(i, button);
      }
    }

    return this;
  }

  public enum FillBorder {
    LEFT,
    RIGHT,
    BOTTOM,
    TOP
  }
}
