package me.ikevoodoo.lssmp.commands.recipe;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.helix.api.config.Configuration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.ikevoodoo.lssmp.Constants.RECIPE_SCREEN_ID;

public class RecipeCommand extends HelixCommand {

    private final Configuration configuration;

    public RecipeCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create(this.configuration.getValue("name"))
                .permission(this.configuration.getValue("permission"));
    }

    @Override
    public CommandExecutionResult handlePlayerSender(@NotNull Player player, @NotNull ArgumentList args) {
        var res = Helix.screens().open(player, RECIPE_SCREEN_ID, null);
        if (!res.success()) {
            player.sendMessage("§cUnable to open recipe screen, sorry!");
            return CommandExecutionResult.FAILURE;
        }

        return CommandExecutionResult.HANDLED;
    }
}
