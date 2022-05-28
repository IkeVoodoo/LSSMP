package me.ikevoodoo.lssmp.commands.reload;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SMPCommand {
    public ReloadCommand(SMPPlugin plugin) {
        super(plugin, "lsreload", "lssmp.reload");
    }

    @Override
    public boolean execute(CommandSender commandSender, Arguments arguments) {
        getPlugin().reload();
        commandSender.sendMessage("§6Reloaded LSSMP");
        return true;
    }
}
