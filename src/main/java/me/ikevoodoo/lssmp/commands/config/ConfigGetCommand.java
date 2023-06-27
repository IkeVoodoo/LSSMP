package me.ikevoodoo.lssmp.commands.config;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;

public class ConfigGetCommand extends SMPCommand {
    public ConfigGetCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.ConfigCommand.ConfigGetCommand.name, CommandConfig.ConfigCommand.ConfigGetCommand.name);
        setArgs(
                ConfigCommand.configArgument(plugin),
                ConfigCommand.pathArgument(plugin)
        );
    }

    @Override
    public boolean execute(Context<?> context) {
        var plugin = getPlugin();

        var confName = context.args().get("config", String.class);
        var config = plugin.getConfigHandler().getYmlConfig(confName + ".yml");
        if (config == null) {
            context.source().sendMessage("§cUnknown config §f" + confName + "§c.");
            return true;
        }


        var path = context.args().get("path", String.class);
        if (!config.contains(path)) {
            context.source().sendMessage("§cUnknown option §f" + path + "§c.");
            return true;
        }

        if (config.isConfigurationSection(path)) {
            context.source().sendMessage("§cPath §f" + path + " §cis not a value!");
            return true;
        }

        var value = config.get(path);

        var valueStr = value instanceof String
                ? CommandConfig.ConfigCommand.Messages.stringValue.formatted(value)
                : CommandConfig.ConfigCommand.Messages.value.formatted(value);

        var message = CommandConfig.ConfigCommand.Messages.configGetMessage
                        .replace("%config%", confName)
                        .replace("%path%", path)
                        .replace("%value%", valueStr);

        context.source().sendMessage(message);
        return true;
    }


}
