package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.utils.Util;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.Bukkit;

public class EliminateAllCommand extends SMPCommand {
    protected EliminateAllCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.EliminateCommand.EliminateAllCommand.name, CommandConfig.EliminateCommand.EliminateAllCommand.perms);
    }

    @Override
    public boolean execute(Context<?> contexts) {
        for (var offlinePlayer : Bukkit.getOfflinePlayers()) {
            getPlugin().getJoinActionHandler().runOnceOnJoin(offlinePlayer.getUniqueId(), uuid -> {
                var player = Bukkit.getPlayer(uuid);
                if (player == null) return;

                Util.eliminate(getPlugin(), player);
            });
        }

        for (var player : Bukkit.getOnlinePlayers()) {
            Util.eliminate(getPlugin(), player);
        }

        contexts.source().sendMessage(CommandConfig.EliminateCommand.Messages.eliminatedAllPlayers);
        return true;
    }
}
