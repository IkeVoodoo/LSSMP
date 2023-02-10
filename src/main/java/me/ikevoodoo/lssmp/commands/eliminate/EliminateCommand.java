package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.lssmp.config.bans.BanConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.handlers.EliminationData;
import me.ikevoodoo.smpcore.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class EliminateCommand extends SMPCommand {
    public EliminateCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.EliminateCommand.name, CommandConfig.EliminateCommand.perms);
        registerSubCommands(new EliminateAllCommand(plugin));
    }

    @Override
    public boolean execute(Context<?> context) {
        if(context.args().isEmpty()) {
            context.source().sendMessage(MainConfig.Messages.Errors.specifyAtLeastOne.replace("%s", "player"));
            return true;
        }

        List<Player> players = context.args().getAll(Player.class);

        var defaultBanMessage = MainConfig.Elimination.Bans.banMessage;
        var standardBanTime = StringUtils.parseBanTime(MainConfig.Elimination.Bans.banTime);

        for(Player player : players) {
            var data = BanConfig.INSTANCE.findHighest(player);

            var banMessage = data == null ? defaultBanMessage : data.banMessage();
            var time = data == null ? standardBanTime : data.time();

            getPlugin().getEliminationHandler().eliminate(player, new EliminationData(banMessage, time));
            player.kickPlayer(banMessage);
        }

        context.source().sendMessage(CommandConfig.EliminateCommand.Messages.eliminatedPlayers.replace("%s", String.valueOf(players.size())));
        return true;
    }

}
