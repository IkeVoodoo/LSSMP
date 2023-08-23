package me.ikevoodoo.lssmp.commands.reset;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.Bukkit;

import java.util.Map;

public class ResetAllCommand extends SMPCommand {
    protected ResetAllCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getResetCommand().getResetAllCommand().name(),
                "permission", commandConfig.getResetCommand().getResetAllCommand().perms()
        )));
    }

    @Override
    public boolean execute(Context<?> context) {
        var defaultHearts = getConfig(MainConfig.class).getEliminationConfig().defaultHearts();
        Bukkit.getOnlinePlayers().forEach(player -> getPlugin().getHealthHelper().setMaxHealthEverywhere(player, defaultHearts * 2));

        for(var offlinePlayer : Bukkit.getOfflinePlayers()) {
            getPlugin().getJoinActionHandler().runOnceOnJoin(offlinePlayer.getUniqueId(), id -> {
                var player = Bukkit.getPlayer(id);
                if (player == null) return;

                getPlugin().getHealthHelper().setMaxHealthEverywhere(player, defaultHearts * 2);
            });
        }

        context.source().sendMessage(
                getPlugin()
                        .getConfigHandler()
                        .getConfig(CommandConfig.class)
                        .getResetCommand()
                        .getMessages()
                        .resetAllPlayers()
        );
        return true;
    }
}