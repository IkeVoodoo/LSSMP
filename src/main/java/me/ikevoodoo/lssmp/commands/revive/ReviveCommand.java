package me.ikevoodoo.lssmp.commands.revive;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReviveCommand extends SMPCommand {
    public ReviveCommand(SMPPlugin plugin) {
        super(plugin, "lsrevive", "lssmp.revive");
        registerSubCommands(new ReviveAllCommand(plugin));
    }

    @Override
    public boolean execute(CommandSender sender, Arguments args) {
        if(args.isEmpty()) {
            sender.sendMessage("§cYou must specify at least one player!");
            return true;
        }

        List<OfflinePlayer> players = args.getAll(OfflinePlayer.class);
        for (OfflinePlayer offlinePlayer : players) {
            if(offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                getPlugin().getEliminationHandler().revive(player);
            } else {
                getPlugin().getEliminationHandler().reviveOffline(offlinePlayer);
            }
        }

        sender.sendMessage("§aRevived " + players.size() + " players!");
        return true;
    }
}
