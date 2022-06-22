package me.ikevoodoo.lssmp.commands.reset;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetAllCommand extends SMPCommand {
    protected ResetAllCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.ResetCommand.ResetAllCommand.name, CommandConfig.ResetCommand.ResetAllCommand.perms);
    }

    @Override
    public boolean execute(CommandSender commandSender, Arguments arguments) {
        Bukkit.getOnlinePlayers().forEach(player -> HealthUtils.set(MainConfig.Elimination.defaultHearts * 2, player));
        for(OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            getPlugin().getJoinActionHandler().runOnJoin(player.getUniqueId(), id -> {
                Player plr = Bukkit.getPlayer(id);
                if(plr != null) {
                    HealthUtils.set(MainConfig.Elimination.defaultHearts * 2, plr);
                }
            });
        }
        commandSender.sendMessage(CommandConfig.ResetCommand.Messages.resetAllPlayers);
        return true;
    }
}