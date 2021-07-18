package me.ikevoodoo.lifestealsmpplugin.commands;

import me.ikevoodoo.lifestealsmpplugin.Configuration;
import me.ikevoodoo.lifestealsmpplugin.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EliminateCommand implements CommandExecutor {

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("lssmp.eliminate")) {
            if(args.length > 0) {
                String person = args[0];
                UUID id = null;
                if(sender instanceof Player pl)
                    id = pl.getUniqueId();
                if((person.equalsIgnoreCase("all")
                        || person.equalsIgnoreCase("@a")) && sender.hasPermission("lssmp.eliminate.all")) {
                    UUID finalId = id;
                    Bukkit.getOnlinePlayers().forEach((player) -> {
                        Configuration.addElimination(player, finalId);
                        if(Configuration.shouldBroadcastBan()) {
                            Bukkit.broadcastMessage(
                                    ChatColor.translateAlternateColorCodes('&',
                                            Configuration.getBroadcastMessage().replace("%player%", player.getName())
                                    )
                            );
                        }

                        Configuration.banID(player.getUniqueId(), Configuration.getBanMessage().replace("%player%", sender.getName()));
                        player.kick(Utils.getFromText(Configuration.getBanMessage().replace("%player%", sender.getName())));
                    });
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Eliminated everyone!");
                } else {
                    Player player = Bukkit.getPlayer(person);
                    if(player != null) {
                        Configuration.addElimination(player, id);
                        if(Configuration.shouldBroadcastBan()) {
                            Bukkit.broadcastMessage(
                                    ChatColor.translateAlternateColorCodes('&',
                                            Configuration.getBroadcastMessage().replace("%player%", player.getName())
                                    )
                            );
                        }

                        Configuration.banID(player.getUniqueId(), Configuration.getBanMessage().replace("%player%", sender.getName()));
                        sender.sendMessage(ChatColor.GOLD + "Eliminated " + ChatColor.AQUA + player.getName());
                        player.kick(Utils.getFromText(Configuration.getBanMessage().replace("%player%", sender.getName())));
                        return false;
                    }

                    OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(person);
                    Configuration.banID(oPlayer.getUniqueId(), Configuration.getBanMessage().replace("%player%", sender.getName()));
                    sender.sendMessage(ChatColor.GOLD + "Eliminated " + ChatColor.AQUA + oPlayer.getName());
                }
            }
        }
        return false;
    }
}
