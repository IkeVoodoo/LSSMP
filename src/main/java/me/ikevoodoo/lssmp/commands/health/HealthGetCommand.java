package me.ikevoodoo.lssmp.commands.health;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import me.ikevoodoo.smpcore.commands.arguments.OptionalFor;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealthGetCommand extends SMPCommand {
    protected HealthGetCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.HealthCommand.HealthGetCommand.name, CommandConfig.HealthCommand.HealthGetCommand.perms);
        setArgs(
                new Argument("player", true, Player.class, OptionalFor.NONE)
        );
    }

    @Override
    public boolean execute(CommandSender commandSender, Arguments arguments) {
        Player player = arguments.get("player", Player.class);
        commandSender.sendMessage(String.format(
                CommandConfig.HealthCommand.Messages.getMessage,
                player.getName(),
                HealthUtils.get(player) / 2
        ));
        return true;
    }
}
