package me.ikevoodoo.lssmp.commands.recipe;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.CommandUsable;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.entity.Player;

import java.util.Map;

public class RecipeCommand extends SMPCommand {
    public RecipeCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getRecipeCommand().name(),
                "permission", commandConfig.getRecipeCommand().perms()
        )));
        setUsable(CommandUsable.PLAYER);
    }

    @Override
    public boolean execute(Context<?> context) {
        getPlugin().getMenuHandler().get("lssmp_recipes_menu").openLast(context.source(Player.class));
        return true;
    }
}
