package me.ikevoodoo.lssmp.commands.reset;

import me.ikevoodoo.lssmp.config.ConfigFile;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ResetCommand extends SMPCommand {
    public ResetCommand(SMPPlugin plugin) {
        super(plugin, "lsreset", "lssmp.reset");
        registerSubCommands(new ResetAllCommand(plugin));
    }

    @Override
    public boolean execute(CommandSender sender, Arguments args) {
        if(args.isEmpty()) {
            sender.sendMessage("§cYou must specify at least one player!");
            return true;
        }

        List<OfflinePlayer> players = args.getAll(OfflinePlayer.class);
        for(OfflinePlayer offlinePlayer : players) {
            if(offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                HealthUtils.set(ConfigFile.Elimination.defaultHearts * 2, player);
            } else {
                getPlugin().getJoinActionHandler().runOnJoin(offlinePlayer.getUniqueId(), id -> {
                    Player player = Bukkit.getPlayer(id);
                    HealthUtils.set(ConfigFile.Elimination.defaultHearts * 2, player);
                });
            }
        }

        sender.sendMessage("§aReset " + players.size() + " players!");
        return true;
    }
}
