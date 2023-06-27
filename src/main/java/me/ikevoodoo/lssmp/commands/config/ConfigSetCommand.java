package me.ikevoodoo.lssmp.commands.config;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.OptionalFor;
import me.ikevoodoo.smpcore.commands.arguments.parsers.ParserRegistry;
import me.ikevoodoo.smpcore.utils.StringUtils;

import java.io.IOException;

public class ConfigSetCommand extends SMPCommand {
    public ConfigSetCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.ConfigCommand.ConfigSetCommand.name, CommandConfig.ConfigCommand.ConfigSetCommand.name);
        setArgs(
                ConfigCommand.configArgument(plugin),
                ConfigCommand.pathArgument(plugin),
                new Argument("value", true, String.class, OptionalFor.NONE)
        );
    }

    @Override
    public boolean execute(Context<?> context) {
        var plugin = getPlugin();

        var confName = context.args().get("config", String.class);
        var parsedConfName = StringUtils.stripExtension(confName) + ".yml";


        var config = plugin.getConfigHandler().getYmlConfig(parsedConfName);
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

        var value = context.args().get("value", String.class);
        var existingValue = config.get(path);
        var parser = ParserRegistry.get(existingValue.getClass());
        if (parser == null) {
            context.source().sendMessage("§cUnable to fetch parser for value §f" + value + "§c!");
            return true;
        }

        var parsed = parser.parse(context.source(), value);
        config.set(path, parsed);
        try {
            plugin.getConfigHandler().saveConfig(parsedConfName);
        } catch (IOException e) {
            context.source().sendMessage("§cUnable to save! Reverting change!");
            config.set(path, existingValue);
            return true;
        }


        var valueStr = parsed instanceof String
                ? CommandConfig.ConfigCommand.Messages.stringValue.formatted(parsed)
                : CommandConfig.ConfigCommand.Messages.value.formatted(parsed);

        var message = CommandConfig.ConfigCommand.Messages.configSetMessage
                        .replace("%config%", confName)
                        .replace("%path%", path)
                        .replace("%value%", valueStr);

        context.source().sendMessage(message);
        return true;
    }


}
