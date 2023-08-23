package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.utils.Util;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.Bukkit;

import java.util.Map;

public class EliminateAllCommand extends SMPCommand {
    protected EliminateAllCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getEliminateCommand().getEliminateAllCommand().name(),
                "permission", commandConfig.getEliminateCommand().getEliminateAllCommand().perms()
        )));
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

        contexts.source().sendMessage(getPlugin()
                .getConfigHandler()
                .getConfig(CommandConfig.class)
                .getEliminateCommand()
                .getMessages()
                .eliminatedAllPlayers());
        return true;
    }
}
