package me.ikevoodoo.lssmp.commands.upgrade;

import me.ikevoodoo.lssmp.LSSMP;
import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.utils.FileUtils;
import org.bukkit.Bukkit;

import java.util.Map;

public class UpgradeCommand extends SMPCommand {

    public UpgradeCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getUpgradeCommand().name(),
                "permission", commandConfig.getUpgradeCommand().perms()
        )));
    }

    @Override
    public boolean execute(Context<?> context) {
        if (context.args().get(0, "").equalsIgnoreCase("force")) {
            FileUtils.delete(getPlugin().getDataFolder());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            return true;
        }

        var messages = getConfig(CommandConfig.class).getUpgradeCommand().getMessages();

        if (getConfig(MainConfig.class).doNotTouch_configVersion() == LSSMP.CURRENT_CONFIG_VERSION) {
            context.source().sendMessage(messages.noUpgradeNeeded());
            return true;
        }

        if (context.args().get(0, "").equalsIgnoreCase("confirm")) {
            FileUtils.delete(getPlugin().getDataFolder());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            return true;
        }

        context.source().sendMessage(messages.warning());
        return true;
    }
}
