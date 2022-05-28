package me.ikevoodoo.lssmp.commands.revive;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import org.bukkit.command.CommandSender;

public class ReviveAllCommand extends SMPCommand {
    protected ReviveAllCommand(SMPPlugin plugin) {
        super(plugin, "all", "lssmp.revive.all");
    }

    @Override
    public boolean execute(CommandSender commandSender, Arguments arguments) {
        getPlugin().getEliminationHandler().reviveAll();
        commandSender.sendMessage("§aAll players have been revived.");
        return true;
    }
}
