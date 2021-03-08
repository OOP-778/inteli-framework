package com.oop.inteliframework.testplugin;

import com.oop.inteliframework.command.bukkit.BukkitCommandRegistry;
import com.oop.inteliframework.command.element.argument.Arguments;
import com.oop.inteliframework.command.element.argument.NoValueArgument;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.config.configuration.PlainConfig;
import com.oop.inteliframework.config.util.Paths;
import com.oop.inteliframework.config.util.Paths.CopyOption;
import com.oop.intelimenus.InteliMenus;
import com.oop.intelimenus.attribute.AttributeComponent;
import com.oop.intelimenus.attribute.Attributes;
import com.oop.intelimenus.button.builder.IButtonBuilder;
import com.oop.intelimenus.config.MenuConfiguration;
import com.oop.intelimenus.config.MenuLoader;
import com.oop.intelimenus.interfaces.MenuUtil;
import com.oop.intelimenus.menu.simple.MenuBuilder;
import com.oop.orangeengine.item.custom.OItem;
import com.oop.orangeengine.material.OMaterial;
import net.minecraft.server.v1_13_R1.PacketPlayOutCommands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.oop.inteliframework.commons.util.StringFormat.colored;

public class TestPlugin extends JavaPlugin implements Listener {

  private TestPlugin plugin;
  private MenuConfiguration configuration;

  @Override
  public void onEnable() {
    this.plugin = this;
    Bukkit.getPluginManager().registerEvents(this, this);

    if (!getDataFolder().exists())
      getDataFolder().mkdirs();

    BukkitCommandRegistry bukkitCommandRegistry = new BukkitCommandRegistry(this);
    bukkitCommandRegistry.register(
        new Command()
            .labeled("hellotest")
            .addChild(
                Arguments.numberArg()
                    .labeled("numba")
                    .tabComplete(
                        ((executor, element, commandData) -> {
                          return Arrays.asList("1", "2", "3", "4", "5");
                        }))
                    .addChild(
                        new Command()
                            .labeled("edit")
                            .onExecute(
                                ((executor, commandData) -> {
                                  System.out.println("HELLO!: " + commandData.hasKey("--silent"));
                                }))
                            .filters()
                            .add(
                                "showFilter",
                                (executor, data) -> {
                                  Double numba = data.getAs("numba", Double.class);
                                  return numba > 1;
                                })
                            .element()
                            .addChild(
                                    new NoValueArgument()
                                        .labeled("--silent")
                            )
                    )
            )
    );

    InteliMenus menus =
        InteliMenus.register(this, itemStack -> new MenuItemBuilder(new OItem(itemStack)));

    MenuLoader menuLoader = new MenuLoader();
    menuLoader.setItemProvider(material -> OMaterial.matchMaterial(material).parseItem());

    Paths.copyFileFromJar(
        "menu.yml", getDataFolder(), CopyOption.COPY_IF_NOT_EXIST, TestPlugin.class);
    configuration =
        new MenuConfiguration(new PlainConfig(new File(getDataFolder(), "menu.yml")), menuLoader);

    menus.registerMenuUtil(
        new MenuUtil() {
          @Override
          public void ensureSync(Runnable runnable) {
            Bukkit.getScheduler().runTask(plugin, runnable);
          }

          @Override
          public void async(Runnable runnable) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
          }

          @Override
          public void playSound(String name, float volume, float pitch, float yaw) {}
        });
  }

  @EventHandler
  public void onChat(AsyncPlayerChatEvent event) {
    //        TestMenu testMenu = new TestMenu(event.getPlayer(), configuration);
    //        testMenu.openAction(null);

    List<Integer> ints = new ArrayList<>();
    IntStream.range(10, 36).forEach(ints::add);

    MenuBuilder.pagedMenu(int.class)
        .who(event.getPlayer())
        .objectsProvider(() -> ints)
        .pagedButtonBuilder(
            player ->
                IButtonBuilder.of(
                        new OItem(OMaterial.CREEPER_HEAD)
                            .setDisplayName("&cI: " + player)
                            .getItemStack())
                    .clickTrigger(
                        trigger -> {
                          System.out.println("Clicked on " + player);
                          trigger.setCancelled(true);
                        })
                    .toButton())
        .rows(4)
        .title("&cTest Paged Menu - &4{currentPage}&8/&c{pages}")
        .designer()
        .row(
            1,
            "@@@@A@@@@",
            '@',
            new IButtonBuilder().item(new ItemStack(Material.STAINED_GLASS_PANE)).toButton(),
            'A',
            IButtonBuilder.of(new OItem(Material.ANVIL).setDisplayName("Text").getItemStack())
                .addAnimation(
                    animation -> {
                      animation.frames(4);
                      animation.shouldGoBack(true);
                      animation.interval(20);
                      animation.repeat(true);
                      animation.onFrame(
                          (frame, anim, button) -> {
                            int charIndex = frame - 1;
                            if (!button.getCurrentItem().isPresent()) {
                              return;
                            }

                            ItemStack itemStack = button.getCurrentItem().get().get();
                            itemStack = itemStack.clone();

                            ItemMeta itemMeta = itemStack.getItemMeta();

                            String displayName = itemMeta.getDisplayName();
                            String newDisplayName = "";

                            char[] chars = displayName.toCharArray();
                            for (int i = 0; i < chars.length; i++) {
                              if (i != charIndex) {
                                newDisplayName += "&5" + chars[i];
                              } else {
                                newDisplayName += "&b&l" + chars[i] + "&5";
                              }
                            }

                            itemMeta.setDisplayName(colored(newDisplayName));
                            itemStack.setItemMeta(itemMeta);

                            Objects.requireNonNull(button.getCurrentMenu(), "menu is null")
                                .getInventoryData()
                                .updateItem(
                                    Objects.requireNonNull(
                                            button.getParent(), "button parent is null")
                                        .getIndex(),
                                    itemStack);
                          });
                    })
                .toButton())
        .row(
            4,
            "@@@P@N@@@",
            '@',
            new IButtonBuilder().item(new ItemStack(Material.STAINED_GLASS_PANE)).toButton(),
            'N',
            IButtonBuilder.of()
                .addAttribute(Attributes.NEXT_PAGE.get())
                .addState(
                    "page-available",
                    new OItem(Material.ARROW).setDisplayName("Next Page").getItemStack())
                .addState("page-not-available", Material.STAINED_GLASS_PANE)
                .toButton(),
            'P',
            IButtonBuilder.of()
                .addAttribute(Attributes.PREVIOUS_PAGE.get())
                .addState(
                    "page-available",
                    new OItem(Material.ARROW).setDisplayName("Previous Page").getItemStack())
                .addState("page-not-available", Material.STAINED_GLASS_PANE)
                .toButton())
        .fillEmpty(
            () ->
                IButtonBuilder.of(
                        new OItem(Material.BARRIER).setDisplayName("&c???").getItemStack())
                    .addAttribute(Attributes.PLACEHOLDER)
                    .toButton())
        .menu()
        .applyComponent(
            AttributeComponent.class, comp -> comp.addAttribute(Attributes.RETURN_ON_CLOSE))
        .openAction(null);
  }

  @Override
  public void onDisable() {
    InteliMenus.getInteliMenus().disable();
  }

  public static class MenuItemBuilder
      implements com.oop.intelimenus.interfaces.MenuItemBuilder<MenuItemBuilder> {

    private final OItem item;

    public MenuItemBuilder(OItem item) {
      this.item = item;
    }

    @Override
    public ItemStack getItem() {
      return item.getItemStack();
    }

    @Override
    public MenuItemBuilder replace(String what, Object to) {
      item.replace(what, to);
      return this;
    }

    @Override
    public MenuItemBuilder replace(Function<String, String> parser) {
      return this;
    }

    @Override
    public List<String> getLore() {
      return item.getLore();
    }

    @Override
    public MenuItemBuilder lore(List<String> newLore) {
      item.setLore(newLore);
      return this;
    }

    @Override
    public MenuItemBuilder appendLore(String line) {
      item.appendLore(line);
      return this;
    }

    @Override
    public MenuItemBuilder appendLore(Collection<String> lines) {
      for (String line : lines) {
        appendLore(line);
      }
      return this;
    }

    @Override
    public MenuItemBuilder displayName(String newDisplayName) {
      item.setDisplayName(newDisplayName);
      return this;
    }

    @Override
    public MenuItemBuilder clone() {
      return new MenuItemBuilder(item.clone());
    }
  }
}
