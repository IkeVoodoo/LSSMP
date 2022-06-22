package me.ikevoodoo.lssmp.commands.health;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import org.bukkit.command.CommandSender;

public class HealthCommand extends SMPCommand {
    public HealthCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.HealthCommand.name, CommandConfig.HealthCommand.perms);
        registerSubCommands(
                new HealthSetCommand(plugin),
                new HealthAddCommand(plugin),
                new HealthSubCommand(plugin),
                new HealthGetCommand(plugin)
        );
    }

    @Override
    public boolean execute(CommandSender commandSender, Arguments arguments) {
        commandSender.sendMessage("§cUsage: §f/lshealth <set|add|sub|get>");
        return true;
    }
}
