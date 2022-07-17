package me.ikevoodoo.lssmp.commands.revive;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;

public class ReviveAllCommand extends SMPCommand {
    protected ReviveAllCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.ReviveCommand.ReviveAllCommand.name, CommandConfig.ReviveCommand.ReviveAllCommand.perms);
    }

    @Override
    public boolean execute(Context<?> context) {
        getPlugin().getEliminationHandler().reviveAll();
        context.source().sendMessage(CommandConfig.ReviveCommand.Messages.revivedAllPlayers);
        return true;
    }
}
