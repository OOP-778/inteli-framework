package com.oop.inteliframework.item.type.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;

@Getter
@AllArgsConstructor
public enum BookGeneration {
  ORIGINAL(Generation.ORIGINAL),
  COPY_OF_ORIGINAL(Generation.COPY_OF_COPY),
  COPY_OF_COPY(Generation.COPY_OF_COPY),
  TATTERED(Generation.TATTERED);

  private final BookMeta.Generation bukkitGeneration;
}
