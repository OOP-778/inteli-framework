package com.oop.inteliframework.recipe.shaped;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

@Accessors(chain = true, fluent = true)
public class ShapedRecipeBuilder {

    @NonNull
    private char[][] pattern;

    private ItemStack result;

    @Setter
    private Function<Player, ItemStack> resultRequester;

    private Map<Character, ItemStack> charToItem = Maps.newHashMap();

    protected ShapedRecipeBuilder() {}

    public static ShapedRecipeBuilder builder() {
        return new ShapedRecipeBuilder();
    }

    public ShapedRecipeBuilder item(char character, @NonNull ItemStack itemStack) {
        Objects.requireNonNull(pattern, "Pattern first should be set!");

        charToItem.put(character, itemStack);
        return this;
    }

    public ShapedRecipeBuilder pattern(String ...pattern) {
        return pattern(Arrays.asList(pattern));
    }

    public ShapedRecipeBuilder pattern(List<String> pattern) {
        Preconditions.checkArgument(pattern.size() <= 3, "Recipe pattern cannot be longer than 3!");

        int x = 0, y = 0;
        this.pattern = new char[pattern.size()][pattern.size()];
        for (String pattern_line : pattern) {
            for (char c : pattern_line.toCharArray()) {
                if (c == ' ') continue;

                this.pattern[y][x] = c;
                x++;
            }

            x = 0;
            y++;
        }

        return this;
    }

    public ShapedRecipe build() {
        int len = pattern.length;
        ItemStack[][] matrix = new ItemStack[len][len];

        IntStream.range(0, len).forEach(y -> {
            IntStream.range(0, len).forEach(x -> {
                char charAt = pattern[y][x];
                ItemStack itemStack = charToItem.get(charAt);
                if (itemStack == null)
                    return;

                matrix[y][x] = itemStack;
            });
        });

        return new ShapedRecipe(matrix, result, resultRequester);
    }

    public ShapedRecipeBuilder result(@NonNull ItemStack result) {
        this.result = result;
        return this;
    }
}
