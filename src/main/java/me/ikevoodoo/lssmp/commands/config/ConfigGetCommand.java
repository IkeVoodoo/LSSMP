package me.ikevoodoo.lssmp.commands.config;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;

import java.util.Map;

public class ConfigGetCommand extends SMPCommand {
    public ConfigGetCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getConfigCommand().getConfigGetCommand().name(),
                "permission", commandConfig.getConfigCommand().getConfigGetCommand().perms()
        )));
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

        var cfg = plugin.getConfigHandler().getConfig(CommandConfig.class).getConfigCommand().getMessages();

        var valueStr = value instanceof String
                ? cfg.stringValue().formatted(value)
                : cfg.value().formatted(value);

        var message = cfg.configGetMessage()
                        .replace("%config%", confName)
                        .replace("%path%", path)
                        .replace("%value%", valueStr);

        context.source().sendMessage(message);
        return true;
    }


}
