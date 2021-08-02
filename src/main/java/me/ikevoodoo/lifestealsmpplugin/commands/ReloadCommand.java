package me.ikevoodoo.lifestealsmpplugin.commands;

import me.ikevoodoo.lifestealsmpplugin.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.command.*;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("lssmp.reload") || sender instanceof ConsoleCommandSender) {
            Configuration.reload();
            sender.sendMessage(ChatColor.GOLD + "Reloaded!");
        }
        return false;
    }
}
