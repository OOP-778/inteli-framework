package com.oop.inteliframework.item.api.holder;

import com.oop.inteliframework.item.api.SimpleInteliMeta;
import com.oop.inteliframework.item.comp.InteliEnchantment;
import lombok.NonNull;
import org.bukkit.enchantments.Enchantment;

import java.util.Map;

/** Hold all and control item enchants */
public interface EnchantHolder<T extends SimpleInteliMeta> {

  /**
   * @param enchant New item enchant (As {@link InteliEnchantment})
   * @throws NullPointerException Is enchant is null
   */
  T enchant(final @NonNull InteliEnchantment enchant);

  /**
   * @param enchant New item enchant (As {@link InteliEnchantment})
   * @param level Enchantment level
   * @throws NullPointerException Is enchant is null
   */
  T enchant(final @NonNull InteliEnchantment enchant, int level);

  /**
   * @param enchant New item enchant (As {@link InteliEnchantment})
   * @param level Enchantment level
   * @param restrictEnchants Restrict enchantment level if last one already there
   * @throws NullPointerException Is enchant is null
   */
  T enchant(final @NonNull InteliEnchantment enchant, int level, final boolean restrictEnchants);

  /**
   * @param enchant Enchantment name to remove (As {@link InteliEnchantment})
   * @throws NullPointerException Is enchant is null
   */
  T removeEnchant(final @NonNull InteliEnchantment enchant);

  /**
   * Removes all enchants
   */
  T removeEnchants();

  /**
   * @return Map of applyable enchants
   * @throws NullPointerException If enchants is null
   */
  @NonNull
  Map<Enchantment, Integer> enchants();
}
