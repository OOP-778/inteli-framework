package com.oop.inteliframework.item.type.book;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import lombok.NonNull;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import static com.oop.inteliframework.commons.util.StringFormat.colored;

public class InteliBookMeta extends AbstractInteliItemMeta<BookMeta, InteliBookMeta> {
  public InteliBookMeta(@NonNull BookMeta meta) {
    super(meta, s -> new InteliBookMeta(s.asBukkitMeta().clone()));
  }

  public InteliBookMeta() {
    this(InteliMaterial.WRITABLE_BOOK);
  }

  public InteliBookMeta(@NonNull InteliMaterial material) {
    this((BookMeta) material.parseItem().getItemMeta());
  }

  public InteliBookMeta title(final @NonNull String title) {
    asBukkitMeta().setTitle(colored(title));
    return this;
  }

  public InteliBookMeta author(final @NonNull String author) {
    asBukkitMeta().setAuthor(colored(author));
    return this;
  }

  public InteliBookMeta generation(final @NonNull BookGeneration generation) {
    asBukkitMeta().setGeneration(generation.getBukkitGeneration());
    return this;
  }

  public InteliBookMeta setPage(final int page, @NotNull String data) {
    asBukkitMeta().setPage(page, colored(data));
    return this;
  }

  public InteliBookMeta setPages(final @NotNull String... pages) {
    // asBukkitMeta().setPages(ArrayFormat.colored(pages));
    return this;
  }

  public InteliBookMeta addPages(final @NotNull String... pages) {
    // asBukkitMeta().addPage(ArrayFormat.colored(pages));
    return this;
  }
}
