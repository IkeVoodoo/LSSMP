package me.ikevoodoo.lssmp.commands.revive;

import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.helix.api.commands.parsers.PlayerParser;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.lssmp.elimination.EliminationManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReviveCommand extends HelixCommand {

    private final Configuration configuration;
    private final EliminationManager eliminationManager;

    public ReviveCommand(Configuration configuration, EliminationManager eliminationManager) {
        this.configuration = configuration;
        this.eliminationManager = eliminationManager;
    }

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create(this.configuration.getValue("name"))
                .childCommand(new ReviveAllCommand(this.eliminationManager))
                .permission(this.configuration.getValue("permission"))
                .argument("victim", PlayerParser.ALL);
    }

    @Override
    public CommandExecutionResult handleGenericSender(@NotNull CommandSender sender, @NotNull ArgumentList args) {
        var resetting = args.<OfflinePlayer>getArgument("victim");

        final var revived = this.eliminationManager.tryRevive(resetting, sender instanceof Player reviver ? reviver : null);
        if (!revived) {
            sender.sendMessage("§aGood news! That player is not eliminated!");
            return CommandExecutionResult.HANDLED;
        }
        
        sender.sendMessage("§aRevived §3" + resetting.getName());

        return CommandExecutionResult.HANDLED;
    }
}
