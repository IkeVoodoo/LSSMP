package me.ikevoodoo.lssmp.commands.reset;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
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
        super(plugin, CommandConfig.ResetCommand.name, CommandConfig.ResetCommand.perms);
        registerSubCommands(new ResetAllCommand(plugin));
    }

    @Override
    public boolean execute(CommandSender sender, Arguments args) {
        if(args.isEmpty()) {
            sender.sendMessage(MainConfig.Messages.Errors.specifyAtLeastOne.replace("%s", "player"));
            return true;
        }

        List<OfflinePlayer> players = args.getAll(OfflinePlayer.class);
        for(OfflinePlayer offlinePlayer : players) {
            if(offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                if (player != null)
                    HealthUtils.set(MainConfig.Elimination.defaultHearts * 2, player);
            } else {
                getPlugin().getJoinActionHandler().runOnJoin(offlinePlayer.getUniqueId(), id -> {
                    Player player = Bukkit.getPlayer(id);
                    if (player != null)
                        HealthUtils.set(MainConfig.Elimination.defaultHearts * 2, player);
                });
            }
        }

        sender.sendMessage(CommandConfig.ResetCommand.Messages.resetPlayers.replace("%s", "" + players.size()));
        return true;
    }
}
