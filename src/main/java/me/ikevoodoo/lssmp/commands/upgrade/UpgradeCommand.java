package me.ikevoodoo.lssmp.commands.upgrade;

import me.ikevoodoo.lssmp.LSSMP;
import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.utils.FileUtils;
import org.bukkit.Bukkit;

public class UpgradeCommand extends SMPCommand {

    public UpgradeCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.UpgradeCommand.name, CommandConfig.UpgradeCommand.perms);
    }

    @Override
    public boolean execute(Context<?> context) {
        if (context.args().get(0, "").equalsIgnoreCase("force")) {
            FileUtils.delete(getPlugin().getDataFolder());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            return true;
        }
        if (MainConfig.doNotTouch_configVersion == LSSMP.CURRENT_CONFIG_VERSION) {
            context.source().sendMessage(CommandConfig.UpgradeCommand.Messages.noUpgradeNeeded);
            return true;
        }
        if (context.args().get(0, "").equalsIgnoreCase("confirm")) {
            FileUtils.delete(getPlugin().getDataFolder());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            return true;
        }
        context.source().sendMessage(CommandConfig.UpgradeCommand.Messages.warning);
        return true;
    }
}
