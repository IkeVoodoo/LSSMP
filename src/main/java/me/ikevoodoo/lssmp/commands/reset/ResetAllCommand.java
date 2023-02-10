package me.ikevoodoo.lssmp.commands.reset;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ResetAllCommand extends SMPCommand {
    protected ResetAllCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.ResetCommand.ResetAllCommand.name, CommandConfig.ResetCommand.ResetAllCommand.perms);
    }

    @Override
    public boolean execute(Context<?> context) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            getPlugin().getHealthHelper().setMaxHearts(player, MainConfig.Elimination.defaultHearts);
        });
        for(OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            getPlugin().getJoinActionHandler().runOnJoin(player.getUniqueId(), id -> {
                Player plr = Bukkit.getPlayer(id);
                if(plr != null) {
                    getPlugin().getHealthHelper().setMaxHealthEverywhere(plr, MainConfig.Elimination.defaultHearts);
                }
            });
        }
        context.source().sendMessage(CommandConfig.ResetCommand.Messages.resetAllPlayers);
        return true;
    }
}