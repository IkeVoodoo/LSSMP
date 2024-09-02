package me.ikevoodoo.lssmp;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class LifestealInit extends JavaPlugin implements Listener {

    private Object plugin;

    @Override
    public void onEnable() {
        var helix = Bukkit.getPluginManager().getPlugin("Helix");

        if (helix == null) {
            getLogger().log(Level.SEVERE, "========================");
            getLogger().log(Level.SEVERE, "Lifesteal requires Helix, please download it from: https://www.spigotmc.org/resources/helix.119149/");
            getLogger().log(Level.SEVERE, "To install Helix, simply add it to your plugins folder.");
            getLogger().log(Level.SEVERE, "The plugin will NOT work until that is done.");
            getLogger().log(Level.SEVERE, "========================");

            getServer().getPluginManager().registerEvents(this, this);
            return;
        }

        var inHelix = getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getName().equals("Helix");

        if (!inHelix) {
            getLogger().log(Level.INFO, "Lifesteal is being moved to the correct directory and will be disabled for a short amount of time.");

            ((me.ikevoodoo.helix.BukkitHelixProvider) helix).movePlugin(this);
            return;
        }

        this.plugin = new Lifesteal(this);
        ((Lifesteal) this.plugin).onEnable(this);
    }

    @Override
    public void reloadConfig() {
        ((Lifesteal) this.plugin).reloadConfig(this);
    }

    @Override
    public void onDisable() {
        if (this.plugin == null) return;

        ((Lifesteal) this.plugin).onDisable(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        var id = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskTimer(this, task -> {
            var player = Bukkit.getPlayer(id);
            if (player == null) {
                task.cancel();
                return;
            }

            player.sendMessage("§c========================");
            player.sendMessage("§cLifesteal requires Helix, please download it from: https://www.spigotmc.org/resources/helix.119149/");
            player.sendMessage("§cTo install Helix, simply add it to your plugins folder.");
            player.sendMessage("§cThe plugin will NOT work until that is done.");
            player.sendMessage("§c========================");
        }, 0, 20 * 60L);
    }


}
