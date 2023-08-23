package me.ikevoodoo.lssmp.commands.debug;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;

import java.util.Map;

public class DebugCommand extends SMPCommand {
    public DebugCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getDebugCommand().name(),
                "permission", commandConfig.getDebugCommand().perms()
        )));
    }

    @Override
    public boolean execute(Context<?> context) {
        context.source().sendMessage("§cThe debug command is not yet released!");
        return true;
    }
}
