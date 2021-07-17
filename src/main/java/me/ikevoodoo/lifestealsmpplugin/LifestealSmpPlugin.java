package me.ikevoodoo.lifestealsmpplugin;

import me.ikevoodoo.lifestealsmpplugin.commands.EliminateCommand;
import me.ikevoodoo.lifestealsmpplugin.commands.ReloadCommand;
import me.ikevoodoo.lifestealsmpplugin.commands.ReviveCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class LifestealSmpPlugin extends JavaPlugin {

    private static LifestealSmpPlugin instance;

    // IntelliJ NPE warnings for getCommand
    @SuppressWarnings("all")
    @Override
    public void onEnable() {
        instance = this;
        Configuration.init();
        getCommand("lsreload").setExecutor(new ReloadCommand());
        getCommand("lseliminate").setExecutor(new EliminateCommand());
        getCommand("lsrevive").setExecutor(new ReviveCommand());
    }
    
    public static LifestealSmpPlugin getInstance() {
        return instance;
    }


}
