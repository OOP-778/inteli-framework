package com.oop.inteliframework.item.type;

import com.oop.inteliframework.commons.util.StringFormat;
import com.oop.inteliframework.item.api.SimpleInteliLore;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.NonNull;

import static com.oop.inteliframework.commons.util.StringFormat.colorizeCollection;

public class InteliLore implements SimpleInteliLore<InteliLore> {
  private @NonNull List<String> lore = new ArrayList<>();

  @Override
  public InteliLore append(@NonNull String... lines) {
    for (String s : lines) append(s);
    return this;
  }

  @Override
  public InteliLore append(@NonNull String line) {
    lore.add(StringFormat.colored(line));
    return this;
  }

  @Override
  public InteliLore replace(int lineNumber, @NonNull Function<String, String> supplier) {
    lore = lore.stream()
        .filter(e -> e.equals(lore.get(lineNumber)))
        .map(supplier)
        .collect(Collectors.toList());
    return this;
  }

  @Override
  public InteliLore replace(@NonNull Predicate<String> filter,
      @NonNull Function<String, String> supplier) {
    lore = lore.stream()
        .filter(filter)
        .map(supplier)
        .collect(Collectors.toList());
    return this;
  }

  @Override
  public InteliLore supplier(@NonNull Consumer<List<String>> supplier) {
    supplier.accept(lore);
    return this;
  }

  @Override
  public InteliLore lore(@NonNull List<String> newLore) {
    lore = colorizeCollection(newLore);
    return this;
  }

  @Override
  public @NonNull List<String> lore() {
    return lore;
  }

}
