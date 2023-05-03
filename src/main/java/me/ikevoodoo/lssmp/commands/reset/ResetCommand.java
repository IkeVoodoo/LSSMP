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
import java.util.UUID;

public class ResetCommand extends SMPCommand {
    public ResetCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.ResetCommand.name, CommandConfig.ResetCommand.perms);
        registerSubCommands(new ResetAllCommand(plugin));
    }

    @Override
    public boolean execute(Context<?> context) {
        if(context.args().isEmpty()) {
            context.source().sendMessage(MainConfig.Messages.Errors.specifyAtLeastOne.replace("%s", "player"));
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

        context.source().sendMessage(CommandConfig.ResetCommand.Messages.resetPlayers.replace("%s", String.valueOf(players.size())));
        return true;
    }

    private void handlePlayer(UUID id) {
        this.handlePlayer(Bukkit.getPlayer(id));
    }

    private void handlePlayer(Player player) {
        if (player == null) return;

        getPlugin().getHealthHelper().setMaxHealthEverywhere(player, MainConfig.Elimination.defaultHearts * 2);
    }
}
