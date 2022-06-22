package me.ikevoodoo.lssmp.commands.recipe;

import me.ikevoodoo.lssmp.LSSMP;
import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.CommandUsable;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RecipeCommand extends SMPCommand {
    public RecipeCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.RecipeCommand.name, CommandConfig.RecipeCommand.perms);
        setUsable(CommandUsable.PLAYER);
    }

    @Override
    public boolean execute(CommandSender commandSender, Arguments arguments) {
        getPlugin().getMenuHandler().get(LSSMP.LSSMP_MENU).open((Player) commandSender);
        return true;
    }
}
