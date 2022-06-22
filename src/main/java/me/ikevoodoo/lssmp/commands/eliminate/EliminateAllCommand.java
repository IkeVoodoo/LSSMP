package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class EliminateAllCommand extends SMPCommand {
    protected EliminateAllCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.EliminateCommand.EliminateAllCommand.name, CommandConfig.EliminateCommand.EliminateAllCommand.perms);
    }

    @Override
    public boolean execute(CommandSender commandSender, Arguments arguments) {
        getPlugin().getEliminationHandler().eliminateAll();
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(MainConfig.Elimination.Bans.banMessage));
        commandSender.sendMessage(CommandConfig.EliminateCommand.Messages.eliminatedAllPlayers);
        return true;
    }
}
