package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.helix.api.commands.parsers.PlayerParser;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.lssmp.elimination.EliminationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EliminateCommand extends HelixCommand {

    private final Configuration configuration;
    private final EliminationManager eliminationManager;

    public EliminateCommand(Configuration configuration, EliminationManager eliminationManager) {
        this.configuration = configuration;
        this.eliminationManager = eliminationManager;
    }

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create(this.configuration.getValue("name"))
                .childCommand(new EliminateAllCommand(this.eliminationManager))
                .permission(this.configuration.getValue("permission"))
                .argument("victim", PlayerParser.ONLINE);
    }

    @Override
    public CommandExecutionResult handleGenericSender(@NotNull CommandSender sender, @NotNull ArgumentList args) {
        var eliminating = args.<Player>getArgument("victim");

        final var killer = sender instanceof Player senderPlayer ? senderPlayer : null;

        final var result = this.eliminationManager.tryEliminate(eliminating, killer);
        switch (result) {
            case ELIMINATION -> sender.sendMessage("§aEliminated §3" + eliminating.getName());
            case ATTEMPTED_ELIMINATION -> sender.sendMessage("§aAttempted elimination on §3" + eliminating.getName());
            case NO_ACTION -> sender.sendMessage("§aGood news! That player is already eliminated!");
        }

        return CommandExecutionResult.HANDLED;
    }
}
