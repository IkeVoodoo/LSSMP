package me.ikevoodoo.lssmp.commands.reset;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ResetCommand extends SMPCommand {
    public ResetCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getResetCommand().name(),
                "permission", commandConfig.getResetCommand().perms()
        )));
        registerSubCommands(new ResetAllCommand(plugin));
    }

    @Override
    public boolean execute(Context<?> context) {
        if(context.args().isEmpty()) {
            context.source().sendMessage(
                    getConfig(MainConfig.class)
                            .getMessages()
                            .getErrorMessages().specifyAtLeastOne().replace("%s", "player"));
            return true;
        }

        List<OfflinePlayer> players = context.args().getAll(OfflinePlayer.class);
        for(OfflinePlayer offlinePlayer : players) {
            if(offlinePlayer.isOnline()) {
                this.handlePlayer(offlinePlayer.getPlayer());
                continue;
            }

            getPlugin().getJoinActionHandler().runOnceOnJoin(offlinePlayer.getUniqueId(), this::handlePlayer);
        }

        context.source().sendMessage(
                getConfig(CommandConfig.class)
                        .getResetCommand()
                        .getMessages().resetPlayers().replace("%s", String.valueOf(players.size())));
        return true;
    }

    private void handlePlayer(UUID id) {
        this.handlePlayer(Bukkit.getPlayer(id));
    }

    private void handlePlayer(Player player) {
        if (player == null) return;

        getPlugin().getHealthHelper().setMaxHealthEverywhere(player, getConfig(MainConfig.class).getEliminationConfig().defaultHearts() * 2);
    }
}
