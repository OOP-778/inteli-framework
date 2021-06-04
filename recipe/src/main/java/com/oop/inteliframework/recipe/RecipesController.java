package com.oop.inteliframework.recipe;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.commons.util.InteliVersion;
import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.recipe.shaped.ShapedRecipe;
import com.oop.inteliframework.recipe.shapeless.ShapelessRecipe;
import com.oop.inteliframework.task.InteliTaskFactory;
import com.oop.inteliframework.task.bukkit.BukkitTaskController;
import com.oop.inteliframework.task.bukkit.InteliBukkitTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RecipesController implements Listener {
  private static RecipesController instance;

  static {
    new RecipesController();
  }

  private final List<MatrixPacked> slots2x2in3x3 = new ArrayList<>();
  private final Set<Recipe> recipes = Sets.newConcurrentHashSet();
  private final Map<CraftingInventory, FoundRecipe> foundRecipes = Maps.newHashMap();
  @Setter @Getter boolean overrideDefault = false;

  private RecipesController() {
    instance = this;

    for (int r = 0; r < 2; r++) {
      for (int c = 0; c < 2; c++) {
        MatrixPacked packed =
            new MatrixPacked(r * 3 + c, r * 3 + c + 1, (r + 1) * 3 + c, (r + 1) * 3 + c + 1);
        slots2x2in3x3.add(packed);
      }
    }
  }

  public static RecipesController getInstance() {
    return instance;
  }

  private static boolean removeItem(ItemStack item, int amt, CraftingInventory inv) {
    if (item == null || amt <= 0) return false;

    for (ItemStack currentItem : inv.getContents()) {
      if (currentItem == null
          || currentItem.getType() == Material.AIR
          || !isSimilar(currentItem, item)) continue;

      if (currentItem.getAmount() >= amt) {
        if ((currentItem.getAmount() - amt) <= 0)
          inv.setItem(inv.first(currentItem), new ItemStack(Material.AIR));
        else currentItem.setAmount(currentItem.getAmount() - amt);

        return true;

      } else {
        amt -= currentItem.getAmount();
        inv.setItem(inv.first(currentItem), new ItemStack(Material.AIR));
      }
    }
    return amt <= 0;
  }

  private static boolean isSimilar(ItemStack first, ItemStack second) {
    boolean similar = false;

    if (first == null || second == null) return similar;

    boolean sameTypeId = (first.getType() == second.getType());
    boolean sameDurability = (first.getDurability() == second.getDurability());
    boolean sameHasItemMeta = (first.hasItemMeta() == second.hasItemMeta());
    boolean sameEnchantments = (first.getEnchantments().equals(second.getEnchantments()));
    boolean sameItemMeta = true;

    if (sameHasItemMeta) {
      sameItemMeta = Bukkit.getItemFactory().equals(first.getItemMeta(), second.getItemMeta());
    }

    if (sameTypeId && sameDurability && sameHasItemMeta && sameEnchantments && sameItemMeta) {
      similar = true;
    }
    return similar;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCraftEvent(PrepareItemCraftEvent event) {
    if (event.getRecipe() != null && !overrideDefault) return;

    foundRecipes.remove(event.getInventory());
    event.getInventory().setItem(0, new ItemStack(Material.AIR));

    craftCheck(event.getInventory());
  }

  private void craftCheck(CraftingInventory inventory) {
    for (Recipe recipe : recipes) {
      Map<Integer, InteliPair<ItemStack, ItemStack>> items;
      if (recipe instanceof ShapedRecipe)
        items = checkShapedRecipe((ShapedRecipe) recipe, inventory);
      else items = checkShapelessRecipe((ShapelessRecipe) recipe, inventory);

      if (!items.isEmpty()) {
        foundRecipes.put(inventory, new FoundRecipe(recipe, items));
        inventory.setItem(0, recipe.getResult((Player) inventory.getViewers().get(0)));
        break;
      }
    }
  }

  public void handleClickEvent(InventoryClickEvent event) {
    CraftingInventory inventory = (CraftingInventory) event.getClickedInventory();
    FoundRecipe recipe = foundRecipes.get(inventory);
    if (recipe == null) {
      craftCheck(inventory);
      return;
    }

    if (event.getSlot() == 0) {
      event.setCancelled(true);

      ItemStack result = recipe.getRecipe().getResult((Player) event.getWhoClicked());
      if (event.getWhoClicked().getItemOnCursor().getType() != Material.AIR
          && !isSimilar(result, event.getWhoClicked().getItemOnCursor())) return;

      recipe
          .getInventoryData()
          .forEach(
              (slot, pair) -> {
                if (slot < 0) {
                  removeItem(pair.getKey(), pair.getKey().getAmount(), inventory);

                } else {
                  ItemStack recipeItem = pair.getKey();
                  ItemStack invItem = pair.getValue();
                  if (invItem == null) return;

                  if (invItem.getAmount() == recipeItem.getAmount()) invItem.setAmount(0);
                  else invItem.setAmount(invItem.getAmount() - recipeItem.getAmount());
                }
              });

      if (checkEmpty(inventory)) {
        foundRecipes.remove(inventory);
        inventory.setItem(0, null);

      } else {
        foundRecipes.remove(inventory);
        inventory.setItem(0, null);
        craftCheck(inventory);
      }

      ItemStack cursor = event.getWhoClicked().getItemOnCursor();
      if (cursor.getType() != Material.AIR) {
        cursor.setAmount(cursor.getAmount() + result.getAmount());
        return;
      }

      event
          .getWhoClicked()
          .setItemOnCursor(recipe.getRecipe().getResult((Player) event.getWhoClicked()));
    }
  }

  @EventHandler
  public void onClickEvent(InventoryClickEvent event) {
    if (!(event.getClickedInventory() instanceof CraftingInventory)) return;

    if (InteliVersion.is(8)) {
      new InteliBukkitTask(
              InteliPlatform.getInstance()
                  .safeModuleByClass(InteliTaskFactory.class)
                  .controllerByClass(BukkitTaskController.class)
                  .get())
          .sync(true)
          .delay(500)
          .body($ -> handleClickEvent(event))
          .run();
      return;
    }

    handleClickEvent(event);
  }

  private boolean checkEmpty(CraftingInventory inventory) {
    return Arrays.stream(inventory.getContents())
        .noneMatch(item -> item != null && item.getType() != Material.AIR);
  }

  @EventHandler
  public void onClose(InventoryCloseEvent event) {
    if (!(event.getInventory() instanceof CraftingInventory)) return;
    foundRecipes.remove(event.getInventory());
  }

  private Map<Integer, InteliPair<ItemStack, ItemStack>> checkShapelessRecipe(
      ShapelessRecipe recipe, CraftingInventory inventory) {
    Map<Integer, InteliPair<ItemStack, ItemStack>> items = Maps.newHashMap();

    Map<ItemStack, Integer> found = Maps.newHashMap();
    for (ItemStack itemStack : inventory.getContents()) {
      ItemStack inMap = inMap(itemStack, recipe.getRequired());
      if (inMap == null) continue;

      found.merge(inMap, itemStack.getAmount(), Integer::sum);
    }

    boolean foundRecipe = compareMaps(found, recipe.getRequired());
    if (foundRecipe) {
      AtomicInteger slot = new AtomicInteger(-1);
      found.forEach(
          (itemStack, integer) -> {
            Integer amount = recipe.getRequired().get(itemStack);
            itemStack = itemStack.clone();
            itemStack.setAmount(amount);

            items.put(slot.get(), new InteliPair<>(itemStack, itemStack));
            slot.getAndDecrement();
          });
    }
    return items;
  }

  private Map<Integer, InteliPair<ItemStack, ItemStack>> checkShapedRecipe(
      ShapedRecipe recipe, CraftingInventory inventory) {
    Map<Integer, InteliPair<ItemStack, ItemStack>> items = Maps.newHashMap();

    final int len = inventory.getSize() == 5 ? 2 : 3;
    if (recipe.is3x3() && len == 2) return items;

    final ItemStack[] invMatrix = inventory.getMatrix();
    if (recipe.getLen() == len) {
      items = compareMatrices(recipe, convertInventoryMatrix(invMatrix, len));
      if (!items.isEmpty()) return items;
    } else {
      // We possibly got 2x2 in 3x3
      ItemStack[][] matrix = recipe.getMatrix();
      ItemStack recipeCurrent = matrix[0][0];
      ItemStack recipeLeft = matrix[0][1];
      ItemStack recipeDown = matrix[1][0];
      ItemStack recipeDiag = matrix[1][1];

      boolean recipeFound = false;
      for (MatrixPacked matrixPacked : slots2x2in3x3) {
        ItemStack invCurrent = invMatrix[matrixPacked.getCurrent()];
        ItemStack invLeft = invMatrix[matrixPacked.getLeft()];
        ItemStack invDown = invMatrix[matrixPacked.getDown()];
        ItemStack invDiag = invMatrix[matrixPacked.getDiag()];

        if (itemArrayEquals(
            recipeCurrent,
            recipeLeft,
            recipeDown,
            recipeDiag,
            invCurrent,
            invLeft,
            invDown,
            invDiag)) {
          recipeFound = true;

          // Packet the found recipe into items map
          items.put(matrixPacked.getCurrent(), new InteliPair<>(recipeCurrent, invCurrent));
          items.put(matrixPacked.getLeft(), new InteliPair<>(recipeLeft, invLeft));
          items.put(matrixPacked.getDown(), new InteliPair<>(recipeDown, invDown));
          items.put(matrixPacked.getDiag(), new InteliPair<>(recipeDiag, invDiag));

          break;
        }
      }

      if (!recipeFound) items.clear();

      return items;
    }

    return items;
  }

  private ItemStack[][] convertInventoryMatrix(ItemStack[] invMatrix, int len) {
    ItemStack[][] converted_matrix = new ItemStack[len][len];
    int slot = 0;

    for (int y = 0; y < len; y++) {
      for (int x = 0; x < len; x++) {
        converted_matrix[y][x] = invMatrix[slot];
        slot++;
      }
    }
    return converted_matrix;
  }

  public void register(Recipe... recipes) {
    this.recipes.addAll(new ArrayList<>(Arrays.asList(recipes)));
  }

  // Utilities
  private boolean compareRecipeItem(ItemStack recipeItemStack, ItemStack inventoryItemStack) {
    if (recipeItemStack == null || recipeItemStack.getType() == Material.AIR)
      return inventoryItemStack == null || inventoryItemStack.getType() == Material.AIR;

    if (inventoryItemStack == null || inventoryItemStack.getType() == Material.AIR) return false;
    if (recipeItemStack.getAmount() > inventoryItemStack.getAmount()) return false;
    if (recipeItemStack.getType() != inventoryItemStack.getType()) return false;

    return recipeItemStack.isSimilar(inventoryItemStack);
  }

  private boolean itemArrayEquals(ItemStack... itemStacks) {
    int mid = itemStacks.length / 2;

    ItemStack[] right = Arrays.copyOfRange(itemStacks, 0, mid);
    ItemStack[] left = Arrays.copyOfRange(itemStacks, mid, itemStacks.length);

    for (int i = 0; i < mid; i++) {
      if (!compareRecipeItem(right[i], left[i])) return false;
    }

    return true;
  }

  private Map<Integer, InteliPair<ItemStack, ItemStack>> compareMatrices(
      ShapedRecipe recipe, ItemStack[][] inv_matrix) {
    ItemStack[][] matrix = recipe.getMatrix();
    int len = matrix.length;
    boolean break_bool = false;

    Map<Integer, InteliPair<ItemStack, ItemStack>> items = Maps.newHashMap();

    int slot = 0;
    for (int y = 0; y < len; y++) {
      for (int x = 0; x < len; x++) {
        ItemStack recipeItemStack = matrix[y][x];
        ItemStack inventoryItemStack = inv_matrix[y][x];

        if (!compareRecipeItem(recipeItemStack, inventoryItemStack)) {
          break_bool = true;
          break;
        }

        items.put(slot, new InteliPair<>(recipeItemStack, inventoryItemStack));
        slot++;
      }
      if (break_bool) break;
    }

    if (break_bool) items.clear();

    return items;
  }

  private ItemStack inMap(ItemStack itemStack, Map<ItemStack, Integer> map) {
    return map.keySet().stream()
        .filter(itemStack1 -> compareRecipeItem(itemStack1, itemStack))
        .findFirst()
        .orElse(null);
  }

  private boolean compareMaps(Map<ItemStack, Integer> required, Map<ItemStack, Integer> found) {
    if (required.size() != found.size()) return false;

    boolean passable = false;
    for (Map.Entry<ItemStack, Integer> req : required.entrySet()) {
      int requiredInt = req.getValue();
      int foundedInt = found.get(req.getKey());

      if (foundedInt >= requiredInt) passable = true;
    }

    return passable;
  }
}
