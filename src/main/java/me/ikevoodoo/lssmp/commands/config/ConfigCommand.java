package me.ikevoodoo.lssmp.commands.config;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.OptionalFor;
import me.ikevoodoo.smpcore.config.ConfigUtils;
import me.ikevoodoo.smpcore.utils.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigCommand extends SMPCommand {
    public ConfigCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getConfigCommand().name(),
                "permission", commandConfig.getConfigCommand().perms()
        )));
        registerSubCommands(
                new ConfigGetCommand(plugin),
                new ConfigSetCommand(plugin)
        );
    }

    @Override
    public boolean execute(Context<?> context) {
        context.source().sendMessage("§cPlease use either §fget §cor §fset§c.");
        return true;
    }

    static Argument configArgument(SMPPlugin plugin) {
        return new Argument("config", true, String.class, OptionalFor.NONE, ctx -> {
            var list = new ArrayList<String>();
            for (var conf : plugin.getConfigHandler().listConfigs()) {
                list.add(StringUtils.stripExtension(conf));
            }

            for (var conf : plugin.getConfigHandler().listYmlConfigs()) {
                list.add(StringUtils.stripExtension(conf));
            }

            return list;
        }, true);
    }

    static Argument pathArgument(SMPPlugin plugin) {
        return new Argument("path", true, String.class, OptionalFor.NONE, ctx -> {
            var cfgName = ctx.args().get("config", String.class);
            var cfg = plugin.getConfigHandler().getYmlConfig(cfgName + ".yml");
            if (cfg == null) return List.of();

            if (!ctx.args().has("path")) {
                return cfg.getKeys(false).stream().toList();
            }

            var path = ctx.args().get("path", String.class);
            var section = cfg.getConfigurationSection(path);
            if (section == null) {
                var parts = path.split("\\.");

                ConfigurationSection curr = cfg;

                for (int i = 0; i < parts.length; i++) {
                    var part = parts[i];

                    var sub = curr.getConfigurationSection(part);
                    if (sub != null) {
                        curr = sub;
                        continue;
                    }

                    for (var key : curr.getKeys(false)) {
                        if (!key.startsWith(part) || !curr.isConfigurationSection(key)) continue;

                        curr = curr.getConfigurationSection(key);
                        parts[i] = key;
                        break;
                    }
                }

                path = String.join(".", parts);
                section = cfg.getConfigurationSection(path);

                if (section == null) {
                    section = cfg.getConfigurationSection(StringUtils.stripExtension(path));

                    if (section == null) {
                        return List.of();
                    }
                }
            }


            var paths = ConfigUtils.getAllValuePaths(section, true);
            var equals = path.replaceAll("\\.+$", "").equalsIgnoreCase(section.getCurrentPath());

            var last = StringUtils.getExtension(path);
            var current = section.getCurrentPath();
            return paths
                    .stream()
                    .filter(p -> equals || p.startsWith(last))
                    .map(p -> String.join(".", current, p))
                    .toList();
        }, false);
    }
}
