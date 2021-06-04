package com.oop.inteliframework.recipe.shaped;

import com.oop.inteliframework.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public class ShapedRecipe implements Recipe {
  private ItemStack[][] matrix;
  private ItemStack result;
  private Function<Player, ItemStack> resultRequester;

  public static ShapedRecipeBuilder builder() {
    return new ShapedRecipeBuilder();
  }

  public int getLen() {
    return matrix.length;
  }

  public boolean is3x3() {
    return matrix.length == 3;
  }

  @Override
  public ItemStack getResult(Player player) {
    if (resultRequester == null || player == null) return result;

    return Objects.requireNonNull(
        resultRequester.apply(player), "The result given cannot be null!");
  }
}
