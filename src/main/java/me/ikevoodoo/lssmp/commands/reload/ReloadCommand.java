package me.ikevoodoo.lssmp.commands.reload;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;

public class ReloadCommand extends SMPCommand {
    public ReloadCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.ReloadCommand.name, CommandConfig.ReloadCommand.perms);
    }

    @Override
    public boolean execute(Context<?> context) {
        getPlugin().reload();
        context.source().sendMessage(CommandConfig.ReloadCommand.Messages.reload);
        return true;
    }
}
