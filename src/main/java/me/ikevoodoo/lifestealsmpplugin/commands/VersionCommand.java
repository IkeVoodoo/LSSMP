package me.ikevoodoo.lifestealsmpplugin.commands;

import me.ikevoodoo.lifestealsmpplugin.LifestealSmpPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VersionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(LifestealSmpPlugin.prefix + ChatColor.GOLD + "Current version: " + ChatColor.GOLD + (LifestealSmpPlugin.updateAvailable ?
                LifestealSmpPlugin.getInstance().getDescription().getVersion() + ChatColor.RED + " (OUTDATED)" : LifestealSmpPlugin.getInstance().getDescription().getVersion() + ChatColor.GREEN + " (LATEST)"));
        return true;
    }
}
