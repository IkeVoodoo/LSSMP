package me.ikevoodoo.lifestealsmpplugin.commands;

import me.ikevoodoo.lifestealsmpplugin.Configuration;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReviveCommand implements CommandExecutor {

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("lssmp.revive")) {
            if(args.length > 0) {
                String person = args[0];
                BanList list = Bukkit.getBanList(BanList.Type.IP);
                if((person.equalsIgnoreCase("all")
                        || person.equalsIgnoreCase("@a")) && sender.hasPermission("lssmp.revive.all")) {
                    Configuration.getEliminations().forEach(eliminated -> {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(eliminated);
                        if(player.getName() != null)
                            list.pardon(player.getName());
                        Configuration.revive(eliminated);
                    });
                } else {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(person);
                    if(player.getName() != null) {
                        list.pardon(player.getName());
                    }
                }
            }
        }
        return false;
    }
}
