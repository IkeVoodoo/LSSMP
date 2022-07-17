package me.ikevoodoo.lssmp.commands.recipe;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.CommandUsable;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.entity.Player;

public class RecipeCommand extends SMPCommand {
    public RecipeCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.RecipeCommand.name, CommandConfig.RecipeCommand.perms);
        setUsable(CommandUsable.PLAYER);
    }

    @Override
    public boolean execute(Context<?> context) {
        getPlugin().getMenuHandler().get("lssmp_recipes_menu").openLast(context.source(Player.class));
        return true;
    }
}
