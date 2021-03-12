package com.oop.inteliframework.item.api.holder;

import com.oop.inteliframework.item.api.SimpleInteliMeta;
import java.util.Set;
import lombok.NonNull;
import org.bukkit.inventory.ItemFlag;

/** Hold all and control item flags */
public interface FlagHolder<T extends SimpleInteliMeta> {

  /**
   * @param flag New item flag
   * @throws NullPointerException If flag is null
   */
  T flag(final @NonNull ItemFlag flag);

  /**
   * @param flags List of new flags
   * @throws NullPointerException If flags is null
   */
  T flags(final @NonNull ItemFlag... flags);

  /**
   * @param flag Flag name to remove
   * @throws NullPointerException If flag is null
   */
  T removeFlag(final @NonNull ItemFlag flag);

  /**
   * @return List of applyable flags
   * @throws NullPointerException If flags is null
   */
  @NonNull
  Set<ItemFlag> flags();
}
