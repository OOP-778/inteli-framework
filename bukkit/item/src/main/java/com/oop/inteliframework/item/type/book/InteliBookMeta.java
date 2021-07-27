package com.oop.inteliframework.item.type.book;

import static com.oop.inteliframework.commons.util.StringFormat.colored;

import com.oop.inteliframework.commons.util.StringFormat;
import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

@Getter
public class InteliBookMeta extends AbstractInteliItemMeta<BookMeta, InteliBookMeta> {

  private String title;
  private String author;
  private List<String> pages;

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
    this.title = title;
    return this;
  }

  public InteliBookMeta author(final @NonNull String author) {
    asBukkitMeta().setAuthor(colored(author));
    this.author = author;
    return this;
  }

  public InteliBookMeta setPage(final int page, @NotNull String data) {
    asBukkitMeta().setPage(page, colored(data));
    this.pages.set(page, data);
    return this;
  }

  public InteliBookMeta setPages(final @NotNull String... pages) {
    asBukkitMeta()
        .setPages(StringFormat.colorizeArray(pages));
    this.pages = Arrays.stream(pages).collect(Collectors.toList());
    return this;
  }

  public InteliBookMeta addPages(final @NotNull String... pages) {
    asBukkitMeta().addPage(StringFormat.colorizeArray(pages));
    this.pages.addAll(Arrays.stream(pages).collect(Collectors.toList()));
    return this;
  }
}
