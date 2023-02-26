package me.ikevoodoo.lssmp.commands.debug;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;

public class DebugCommand extends SMPCommand {
    public DebugCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.DebugCommand.name, CommandConfig.DebugCommand.perms);
    }

    @Override
    public boolean execute(Context<?> context) {
        context.source().sendMessage("§cThe debug command is not yet released!");
        return true;
    }
}
