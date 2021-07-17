package me.ikevoodoo.lifestealsmpplugin.commands;

import me.ikevoodoo.lifestealsmpplugin.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("lssmp.reload") || sender instanceof ConsoleCommandSender) {
            Configuration.reload();
            sender.sendMessage(ChatColor.GOLD + "Reloaded!");
        }
        return false;
    }
}
