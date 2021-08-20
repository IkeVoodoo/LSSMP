package me.ikevoodoo.lifestealsmpplugin.commands;

import me.ikevoodoo.lifestealsmpplugin.Configuration;
import me.ikevoodoo.lifestealsmpplugin.LifestealSmpPlugin;
import me.ikevoodoo.lifestealsmpplugin.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;

import java.io.IOException;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("lssmp.reload") || sender instanceof ConsoleCommandSender) {
            Configuration.reload();
            try {
                Bukkit.removeRecipe(LifestealSmpPlugin.heartKey);
                LifestealSmpPlugin.currentHeartRecipe = LifestealSmpPlugin.loadRecipe(LifestealSmpPlugin.heartRecipeFile);
                Bukkit.addRecipe(LifestealSmpPlugin.currentHeartRecipe);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sender.sendMessage(ChatColor.GOLD + "Reloaded!");
        }
        return true;
    }
}
