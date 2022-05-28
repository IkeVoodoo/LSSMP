package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EliminateCommand extends SMPCommand {
    public EliminateCommand(SMPPlugin plugin) {
        super(plugin, "lseliminate", "lssmp.eliminate");
        registerSubCommands(new EliminateAllCommand(plugin));
    }

    @Override
    public boolean execute(CommandSender sender, Arguments args) {
        if(args.isEmpty()) {
            sender.sendMessage("§cYou must specify at least one player!");
            return true;
        }

        List<Player> players = args.getAll(Player.class);
        for(Player player : players) {
            getPlugin().getEliminationHandler().eliminate(player);
            player.kickPlayer("§cYou have been eliminated!");
        }

        sender.sendMessage("§aEliminated " + players.size() + " players!");
        return true;
    }

}
