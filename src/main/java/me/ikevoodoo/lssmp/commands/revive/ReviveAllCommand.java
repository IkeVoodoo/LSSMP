package me.ikevoodoo.lssmp.commands.revive;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import org.bukkit.command.CommandSender;

public class ReviveAllCommand extends SMPCommand {
    protected ReviveAllCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.ReviveCommand.ReviveAllCommand.name, CommandConfig.ReviveCommand.ReviveAllCommand.perms);
    }

    @Override
    public boolean execute(CommandSender commandSender, Arguments arguments) {
        getPlugin().getEliminationHandler().reviveAll();
        commandSender.sendMessage(CommandConfig.ReviveCommand.Messages.revivedAllPlayers);
        return true;
    }
}
