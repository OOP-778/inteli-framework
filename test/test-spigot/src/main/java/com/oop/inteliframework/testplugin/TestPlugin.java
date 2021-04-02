package com.oop.inteliframework.testplugin;

import com.oop.inteliframework.animation.InteliAnimationModule;
import com.oop.inteliframework.dependency.common.CommonLibraryManager;
import com.oop.inteliframework.dependency.common.CommonLogAdapter;
import com.oop.inteliframework.hologram.Hologram;
import com.oop.inteliframework.hologram.HologramController;
import com.oop.inteliframework.plugin.PlatformStarter;
import com.oop.inteliframework.task.InteliTaskFactory;
import com.oop.inteliframework.task.type.inteli.InteliTaskController;
import com.oop.inteliframework.testplugin.command.CommandsTesting;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.markdown.GithubFlavor;
import net.kyori.adventure.text.minimessage.transformation.TransformationType;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.craftbukkit.BukkitComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URLClassLoader;
import java.nio.file.Path;

public class TestPlugin extends JavaPlugin implements Listener, PlatformStarter<TestPlugin> {

  private HologramController hologramController;
  private Hologram hologram;

  public TestPlugin() {}

  @Override
  public void onEnable() {
    if (!getDataFolder().exists()) getDataFolder().mkdirs();

    new CommonLibraryManager(
            new CommonLogAdapter(), (URLClassLoader) getClassLoader(), getDataFolder())
        .load();

    startPlatform();
    registerModule(new InteliTaskFactory().registerController(new InteliTaskController(1)));
    registerModule(new InteliAnimationModule());

    new CommandsTesting(this);
    Bukkit.getPluginManager().registerEvents(this, this);
  }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
      BukkitAudiences bukkitAudiences = BukkitAudiences.create(this);

//      NBTItem nbtItem =
//          new NBTItem(
//              SimpleItemFactory.itemOf(InteliMaterial.DIAMOND)
//                  .applyMeta(
//                      meta -> {
//                        meta.enchant(InteliEnchantment.DAMAGE_ALL, 15);
//                        meta.name("YAY");
//                      })
//                  .asBukkitStack());

      TextComponent deserialize = BukkitComponentSerializer.legacy().deserialize(ChatColor.translateAlternateColorCodes('&', "&cHello!"));
      MiniMessage build = MiniMessage.builder().markdown().build();
      System.out.println(build.serialize(deserialize));

      bukkitAudiences.all().sendMessage(deserialize);
    }

  //  @EventHandler public void onChat(AsyncPlayerChatEvent event) {
  //    File file = new File(getDataFolder(), "testMenu.yml");
  //
  //    InteliMenus.register(this, InteliMenuItemBuilder::new);
  //
  //    InteliMenus.getInteliMenus().registerMenuUtil(new MenuUtil() {
  //      @Override
  //      public void ensureSync(Runnable runnable) {
  //        runnable.run();
  //      }
  //
  //      @Override
  //      public void async(Runnable runnable) {
  //        runnable.run();
  //      }
  //
  //      @Override
  //      public void playSound(String name, float volume, float pitch, float yaw) {
  //
  //      }
  //    });
  //
  //    MenuLoader menuLoader = new MenuLoader();
  //    menuLoader.setItemProvider(name -> InteliMaterial.matchMaterial(name).parseItem());
  //    PlainConfig nodes = new PlainConfig(file);
  //    nodes.load();
  //
  //    new TestMenu(event.getPlayer(), new MenuConfiguration(nodes, menuLoader)).openAction(null);
  //  }

  @Override
  public Path dataDirectory() {
    return getDataFolder().toPath();
  }

  @Override
  public String name() {
    return getDescription().getName();
  }
}
