package me.ikevoodoo.lifestealsmpplugin.commands;

import me.ikevoodoo.lifestealsmpplugin.LifestealSmpPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UpdateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // This is a work in progress, expect it in 1.6.1
        
        if(sender.hasPermission("lssmp.update")) {
            if(LifestealSmpPlugin.updateAvailable) {
                LifestealSmpPlugin.updating = true;
                LifestealSmpPlugin.updateAvailable = false;
            } else {
                sender.sendMessage(LifestealSmpPlugin.prefix + ChatColor.GOLD + "No update available! You are up-to-date.");
            }
        }
        return true;
    }
}
