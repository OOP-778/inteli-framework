package com.oop.inteliframework.recipe.shapeless;

import com.oop.inteliframework.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@AllArgsConstructor
public class ShapelessRecipe implements Recipe {

  @Getter private Map<ItemStack, Integer> required;

  private ItemStack result;
  private Function<Player, ItemStack> resultRequester;

  public static ShapelessRecipeBuilder builder() {
    return new ShapelessRecipeBuilder();
  }

  @Override
  public ItemStack getResult(Player player) {
    if (resultRequester == null || player == null) return result;

    return Objects.requireNonNull(
        resultRequester.apply(player), "The result given cannot be null!");
  }
}
