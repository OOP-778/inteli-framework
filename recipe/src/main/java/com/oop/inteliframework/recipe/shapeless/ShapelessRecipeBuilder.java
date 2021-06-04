package com.oop.inteliframework.recipe.shapeless;

import com.google.common.collect.Maps;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Function;

@Accessors(chain = true, fluent = true)
public class ShapelessRecipeBuilder {

  private Map<ItemStack, Integer> required = Maps.newHashMap();
  private ItemStack result;
  @Setter private Function<Player, ItemStack> resultRequester;

  protected ShapelessRecipeBuilder() {}

  public static ShapelessRecipeBuilder builder() {
    return new ShapelessRecipeBuilder();
  }

  public ShapelessRecipeBuilder required(int amount, ItemStack itemStack) {
    required.put(itemStack, amount);
    return this;
  }

  public ShapelessRecipeBuilder required(ItemStack itemStack) {
    required.put(itemStack, itemStack.getAmount());
    return this;
  }

  public ShapelessRecipeBuilder result(@NonNull ItemStack result) {
    this.result = result;
    return this;
  }

  public ShapelessRecipe build() {
    return new ShapelessRecipe(required, result, resultRequester);
  }
}
