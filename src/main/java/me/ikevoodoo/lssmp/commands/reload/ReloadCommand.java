package me.ikevoodoo.lssmp.commands.reload;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;

import java.util.Map;

public class ReloadCommand extends SMPCommand {
    public ReloadCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getReloadCommand().name(),
                "permission", commandConfig.getReloadCommand().perms()
        )));
    }

    @Override
    public boolean execute(Context<?> context) {
        getPlugin().reload();
        context.source().sendMessage(
                getPlugin()
                .getConfigHandler()
                .getConfig(CommandConfig.class)
                .getReloadCommand()
                .getMessages()
                .reload()
        );
        return true;
    }
}
