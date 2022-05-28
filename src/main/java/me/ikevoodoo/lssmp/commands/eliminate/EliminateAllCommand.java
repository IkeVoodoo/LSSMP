package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class EliminateAllCommand extends SMPCommand {
    protected EliminateAllCommand(SMPPlugin plugin) {
        super(plugin, "all", "lssmp.eliminate.all");
    }

    @Override
    public boolean execute(CommandSender commandSender, Arguments arguments) {
        getPlugin().getEliminationHandler().eliminateAll();
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§cYou have been eliminated."));
        commandSender.sendMessage("§aAll players have been eliminated.");
        return true;
    }
}
