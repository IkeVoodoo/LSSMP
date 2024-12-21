package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.helix.api.commands.parsers.PlayerParser;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.lssmp.elimination.EliminationHelper;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EliminateCommand extends HelixCommand {

    private final Configuration configuration;

    public EliminateCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create(this.configuration.getValue("name"))
                .childCommand(new EliminateAllCommand())
                .permission(this.configuration.getValue("permission"))
                .argument("victim", PlayerParser.ONLINE);
    }

    @Override
    public CommandExecutionResult handleGenericSender(@NotNull CommandSender sender, @NotNull ArgumentList args) {
        var eliminating = args.<Player>getArgument("victim");

        sender.sendMessage("§aEliminated §3" + eliminating.getName());

        EliminationHelper.eliminate(eliminating, sender instanceof Player senderPlayer ? senderPlayer : null);

        return CommandExecutionResult.HANDLED;
    }
}
