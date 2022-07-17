package me.ikevoodoo.lssmp.commands.revive;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class ReviveCommand extends SMPCommand {
    public ReviveCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.ReviveCommand.name, CommandConfig.ReviveCommand.perms);
        registerSubCommands(new ReviveAllCommand(plugin));
    }

    @Override
    public boolean execute(Context<?> context) {
        if(context.args().isEmpty()) {
            context.source().sendMessage(MainConfig.Messages.Errors.specifyAtLeastOne.replace("%s", "player"));
            return true;
        }

        List<OfflinePlayer> players = context.args().getAll(OfflinePlayer.class);
        for (OfflinePlayer offlinePlayer : players) {
            if(offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                if (player != null)
                    getPlugin().getEliminationHandler().revive(player);
            } else {
                getPlugin().getEliminationHandler().reviveOffline(offlinePlayer);
            }
        }

        context.source().sendMessage(CommandConfig.ReviveCommand.Messages.revivedPlayers.replace("%s", "" + players.size()));
        return true;
    }
}
