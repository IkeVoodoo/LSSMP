package me.ikevoodoo.lifestealsmpplugin.commands;

import me.ikevoodoo.lifestealsmpplugin.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EliminateCommand implements CommandExecutor {

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("lssmp.eliminate")) {
            if(args.length > 0) {
                String person = args[0];
                if((person.equalsIgnoreCase("all")
                        || person.equalsIgnoreCase("@a")) && sender.hasPermission("lssmp.eliminate.all")) {
                    Bukkit.getOnlinePlayers().forEach((player) -> {
                        Configuration.addElimination(player);
                        if(Configuration.shouldBroadcastBan()) {
                            Bukkit.broadcastMessage(
                                    ChatColor.translateAlternateColorCodes('&',
                                            Configuration.getBroadcastMessage().replace("%player%", player.getName())
                                    )
                            );
                        }

                        player.banPlayer(ChatColor.translateAlternateColorCodes('&',
                                Configuration.getBanMessage().replace("%player%", sender.getName())
                        ));
                    });
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Eliminated everyone!");
                } else {
                    Player player = Bukkit.getPlayer(person);
                    if(player != null) {
                        Configuration.addElimination(player);
                        if(Configuration.shouldBroadcastBan()) {
                            Bukkit.broadcastMessage(
                                    ChatColor.translateAlternateColorCodes('&',
                                            Configuration.getBroadcastMessage().replace("%player%", player.getName())
                                    )
                            );
                        }

                        player.banPlayer(ChatColor.translateAlternateColorCodes('&',
                                Configuration.getBanMessage().replace("%player%", sender.getName())
                        ));
                        sender.sendMessage(ChatColor.GOLD + "Eliminated " + ChatColor.AQUA + player.getName());
                    } else sender.sendMessage(ChatColor.RED + "Could not find player " + ChatColor.AQUA + person);
                }
            }
        }
        return false;
    }
}
