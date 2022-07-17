package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.Bukkit;

public class EliminateAllCommand extends SMPCommand {
    protected EliminateAllCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.EliminateCommand.EliminateAllCommand.name, CommandConfig.EliminateCommand.EliminateAllCommand.perms);
    }

    @Override
    public boolean execute(Context<?> contexts) {
        getPlugin().getEliminationHandler().eliminateAll();
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(MainConfig.Elimination.Bans.banMessage));
        contexts.source().sendMessage(CommandConfig.EliminateCommand.Messages.eliminatedAllPlayers);
        return true;
    }
}
