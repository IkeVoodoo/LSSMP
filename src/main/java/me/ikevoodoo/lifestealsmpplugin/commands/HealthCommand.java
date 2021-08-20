package me.ikevoodoo.lifestealsmpplugin.commands;

import me.ikevoodoo.lifestealsmpplugin.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Locale;

public class HealthCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("lssmp.hearts")) {
            // He he
            sender.sendMessage(ChatColor.WHITE + "Unknown command. Try /help for a list of commands");
            return true;
        }

        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Correct usage: /lshealth <get|add|remove> <Player|Number> [<Player>]");
            sender.sendMessage(ChatColor.RED + "The last [<Player>] argument is needed only if the previous one is a number");
            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("get"))
                sender.sendMessage(ChatColor.RED + "Correct usage: /lshealth get <Player>");
            else if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))
                sender.sendMessage(ChatColor.RED + "Correct usage: /lshealth " + args[0].toLowerCase(Locale.ROOT) + " <Number> <Player>");
            return true;
        }

        switch (args.length) {
            case 2 -> {
                if (args[0].equalsIgnoreCase("get")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "Player must be online! (Make sure to check your spelling if the player is online!)");
                        break;
                    }

                    double hp = player.getHealth();
                    double hearts = hp - Double.parseDouble(String.valueOf(hp).replaceFirst("\\.0+$", "")) / 2;
                    double percentage = (hp / Utils.getMaxHealth(player).getBaseValue()) * 100;
                    sender.sendMessage(ChatColor.BLUE + "Player " + ChatColor.DARK_AQUA + player.getName() + ChatColor.BLUE + " has " +
                            (percentage > 50 ? ChatColor.GREEN : percentage > 10 ? ChatColor.YELLOW : ChatColor.RED) + "❤ " + hearts + ChatColor.BLUE + " Hearts.");
                    break;
                }
                if (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender) {
                    sender.sendMessage(ChatColor.RED + "Please specify a player!");
                    sender.sendMessage(ChatColor.RED + "/" + label + " " + String.join(" ", args) + "<-- HERE [Missing Player]");
                    break;
                }
                try {
                    double value = Double.parseDouble(args[1]);
                    if (args[0].equalsIgnoreCase("remove")) value = -value;
                    Player player = (Player) sender;
                    modifyHealth(sender, player, value);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Cannot parse argument 2 to a number: " + args[1]);
                }
            }
            case 3 -> {
                if (!(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) return true;
                try {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "Player must be online! (Make sure to check your spelling if the player is online!)");
                        break;
                    }
                    double value = Double.parseDouble(args[2]);
                    if (args[0].equalsIgnoreCase("remove")) value = -value;
                    modifyHealth(sender, player, value);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Cannot parse argument 2 to a number: " + args[1]);
                }
            }
        }
        return true;
    }

    private void modifyHealth(CommandSender sender, Player player, double value) {
        Utils.modifyHealth(player, Utils.parseHearts(value));
        double hp = player.getHealth();
        double hearts = hp - Double.parseDouble(String.valueOf(hp).replaceFirst("\\.0+$", "")) / 2;
        double percentage = (hp / Utils.getMaxHealth(player).getBaseValue()) * 100;
        sender.sendMessage(ChatColor.BLUE + "Player " + ChatColor.DARK_AQUA + player.getName() + ChatColor.BLUE + " you now have "
                + (percentage > 50 ? ChatColor.GREEN : percentage > 10 ? ChatColor.YELLOW : ChatColor.RED) + "❤ " + hearts + ChatColor.BLUE + " Hearts.");
    }
}
