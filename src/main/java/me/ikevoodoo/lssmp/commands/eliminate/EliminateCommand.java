package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EliminateCommand extends SMPCommand {
    public EliminateCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.EliminateCommand.name, CommandConfig.EliminateCommand.perms);
        registerSubCommands(new EliminateAllCommand(plugin));
    }

    @Override
    public boolean execute(CommandSender sender, Arguments args) {
        if(args.isEmpty()) {
            sender.sendMessage(MainConfig.Messages.Errors.specifyAtLeastOne.replace("%s", "player"));
            return true;
        }

        List<Player> players = args.getAll(Player.class);
        for(Player player : players) {
            getPlugin().getEliminationHandler().eliminate(player);
            player.kickPlayer(MainConfig.Elimination.Bans.banMessage);
        }

        sender.sendMessage(CommandConfig.EliminateCommand.Messages.eliminatedPlayers.replace("%s", "" + players.size()));
        return true;
    }

}
