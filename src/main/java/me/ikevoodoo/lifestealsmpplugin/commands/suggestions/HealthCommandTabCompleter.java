package me.ikevoodoo.lifestealsmpplugin.commands.suggestions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;

import java.util.Arrays;
import java.util.List;

public class HealthCommandTabCompleter implements TabCompleter {

    private static final List<String> args0 = Arrays.asList("add", "remove", "get");
    private static final List<String> args1 = List.of("");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(alias.equalsIgnoreCase("lshealth")) {
            if(args.length == 0)
                return args0;

            if(args.length == 1)
                return args[0].equalsIgnoreCase("get") ? Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList() : args1;

            if(args.length == 2)
                return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();
        }
        return null;
    }
}
