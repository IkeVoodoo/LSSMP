package me.ikevoodoo.lifestealsmpplugin;

import me.ikevoodoo.lifestealsmpplugin.bstats.Metrics;
import me.ikevoodoo.lifestealsmpplugin.commands.EliminateCommand;
import me.ikevoodoo.lifestealsmpplugin.commands.ReloadCommand;
import me.ikevoodoo.lifestealsmpplugin.commands.ReviveCommand;
import me.ikevoodoo.lifestealsmpplugin.events.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public final class LifestealSmpPlugin extends JavaPlugin {

    private static LifestealSmpPlugin instance;

    // IntelliJ NPE warnings for getCommand
    @SuppressWarnings("all")
    @Override
    public void onEnable() {
        instance = this;
        saveConfig();
        Configuration.init();
        Metrics metrics = new Metrics(this, 12177);

        metrics.addCustomChart(new Metrics.AdvancedPie("elmiminations", new Callable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() throws Exception {
                Map<String, Integer> valueMap = new HashMap<>();
                valueMap.put(Configuration.shouldEliminate()         ? "Eliminates"                : "Does not eliminate",                1);
                valueMap.put(Configuration.environmentStealsHearts() ? "Environment steals hearts" : "Envirnoment does not steal hearts", 1);
                valueMap.put(Configuration.shouldScaleHealth()       ? "Scales Health"             : "Does not scale health",             1);
                valueMap.put(Configuration.shouldBan()               ? "Bans"                      : "Does not ban",                      1);
                valueMap.put(Configuration.shouldBroadcastBan()      ? "Broadcasts ban"            : "Does not broadcast ban",            1);
                valueMap.put(Configuration.shouldSpectate()          ? "Has spectators"            : "Does not have spectators",          1);
                return valueMap;
            }
        }));
        
        getCommand("lsreload").setExecutor(new ReloadCommand());
        getCommand("lseliminate").setExecutor(new EliminateCommand());
        getCommand("lsrevive").setExecutor(new ReviveCommand());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }
    
    public static LifestealSmpPlugin getInstance() {
        return instance;
    }


}
