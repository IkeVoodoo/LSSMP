package me.ikevoodoo.lssmp.commands.health;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;

import java.util.Map;

public class HealthCommand extends SMPCommand {
    public HealthCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getHealthCommand().name(),
                "permission", commandConfig.getHealthCommand().perms()
        )));
        registerSubCommands(
                new HealthSetCommand(plugin),
                new HealthAddCommand(plugin),
                new HealthSubCommand(plugin),
                new HealthGetCommand(plugin)
        );
    }

    @Override
    public boolean execute(Context<?> context) {
        context.source().sendMessage("§cPlease use either §fset§c, §fadd§c, §fsub §cor §fget§c.");
        return true;
    }
}
