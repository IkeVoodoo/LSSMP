package me.ikevoodoo.lssmp.commands.get;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import me.ikevoodoo.smpcore.commands.arguments.OptionalFor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCommand extends SMPCommand {
    public GiveCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.GiveCommand.name, CommandConfig.GiveCommand.perms);
        setArgs(
                new Argument("item", true, String.class, OptionalFor.NONE),
                new Argument("count", false, Integer.class, OptionalFor.ALL),
                new Argument("player", false, Player.class, OptionalFor.PLAYER)
        );

    }

    @Override
    public boolean execute(CommandSender commandSender, Arguments arguments) {
        getPlugin().getItem(arguments.get("item", String.class)).ifPresentOrElse(customItem -> {
            Player target = arguments.get("player", Player.class, null);
            if (target == null) {
                if (commandSender instanceof Player player) target = player;
                else {
                    commandSender.sendMessage(MainConfig.Messages.Errors.requiresPlayer);
                    return;
                }
            }
            int count = arguments.get("count", Integer.class, 1);

            target.getInventory().addItem(customItem.getItemStack(count));
        }, () -> commandSender.sendMessage(String.format(MainConfig.Messages.Errors.requiresArgument, "item")));
        return true;
    }


}
