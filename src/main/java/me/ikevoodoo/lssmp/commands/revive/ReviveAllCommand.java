package me.ikevoodoo.lssmp.commands.revive;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;

import java.util.Map;

public class ReviveAllCommand extends SMPCommand {
    protected ReviveAllCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getReviveCommand().getReviveAllCommand().name(),
                "permission", commandConfig.getReviveCommand().getReviveAllCommand().perms()
        )));
    }

    @Override
    public boolean execute(Context<?> context) {
        getPlugin().getEliminationHandler().reviveAll();
        context.source().sendMessage(getConfig(CommandConfig.class).getReviveCommand().getMessages().revivedAllPlayers());
        return true;
    }
}
