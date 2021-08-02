package me.ikevoodoo.lifestealsmpplugin.commands;

import me.ikevoodoo.lifestealsmpplugin.Configuration;
import me.ikevoodoo.lifestealsmpplugin.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class ReviveCommand implements CommandExecutor {

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("lssmp.revive")) {
            if(args.length > 0) {
                String person = args[0];
                if((person.equalsIgnoreCase("all")
                        || person.equalsIgnoreCase("@a")) && sender.hasPermission("lssmp.revive.all")) {
                    Configuration.getEliminations().forEach(Configuration::revive);
                    Bukkit.broadcastMessage(Utils.getFromText(ChatColor.GOLD + "Revived everyone!"));
                } else {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(person);
                    List<UUID> eliminations = Configuration.getEliminations();
                    int index = eliminations.indexOf(player.getUniqueId());
                    if(index != -1) {
                        UUID id = eliminations.get(index);
                        Configuration.revive(id);
                        sender.sendMessage(ChatColor.GOLD + "Revived " + ChatColor.AQUA + player.getName());
                        return false;
                    }
                    sender.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.RED + " is not eliminated.");
                }
            }
        }
        return false;
    }
}
