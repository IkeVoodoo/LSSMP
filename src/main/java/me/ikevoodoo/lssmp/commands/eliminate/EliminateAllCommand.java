package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.lssmp.config.bans.BanConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.handlers.EliminationData;
import org.bukkit.Bukkit;

public class EliminateAllCommand extends SMPCommand {
    protected EliminateAllCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.EliminateCommand.EliminateAllCommand.name, CommandConfig.EliminateCommand.EliminateAllCommand.perms);
    }

    @Override
    public boolean execute(Context<?> contexts) {
        var defaultBanMessage = MainConfig.Elimination.Bans.banMessage;
        var standardBanTime = MainConfig.Elimination.Bans.getBanTime();

        for (var player : Bukkit.getOnlinePlayers()) {
            var data = BanConfig.INSTANCE.findHighest(player);

            var banMessage = data == null ? defaultBanMessage : data.banMessage();
            var time = data == null ? standardBanTime : data.time();

            getPlugin().getEliminationHandler().eliminate(player, new EliminationData(banMessage, time));
            player.kickPlayer(banMessage);
        }

        contexts.source().sendMessage(CommandConfig.EliminateCommand.Messages.eliminatedAllPlayers);
        return true;
    }
}
