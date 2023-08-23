package me.ikevoodoo.lssmp.commands.revive;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class ReviveCommand extends SMPCommand {
    public ReviveCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getReviveCommand().name(),
                "permission", commandConfig.getReviveCommand().perms()
        )));
        registerSubCommands(new ReviveAllCommand(plugin));
    }

    @Override
    public boolean execute(Context<?> context) {
        if(context.args().isEmpty()) {
            context.source().sendMessage(getConfig(MainConfig.class).getMessages().getErrorMessages().specifyAtLeastOne().replace("%s", "player"));
            return true;
        }

        List<OfflinePlayer> players = context.args().getAll(OfflinePlayer.class);
        for (OfflinePlayer offlinePlayer : players) {
            if(offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                if (player != null) {
                    getPlugin().getEliminationHandler().revive(player);
                }
            } else {
                getPlugin().getEliminationHandler().reviveOffline(offlinePlayer);
            }
        }

        context.source().sendMessage(getConfig(CommandConfig.class).getReviveCommand().getMessages().revivedPlayers().replace("%s", String.valueOf(players.size())));
        return true;
    }
}
