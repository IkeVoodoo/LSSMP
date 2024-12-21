package me.ikevoodoo.lssmp.commands.revive;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.lssmp.elimination.EliminationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReviveAllCommand extends HelixCommand {

    private final EliminationManager eliminationManager;

    public ReviveAllCommand(EliminationManager eliminationManager) {
        this.eliminationManager = eliminationManager;
    }

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create("all");
    }

    @Override
    public CommandExecutionResult handleGenericSender(@NotNull CommandSender sender, @NotNull ArgumentList args) {
        var tag = Helix.tags().get("elimination");
        var all = tag.listAll();
        if (all.isEmpty()) {
            sender.sendMessage("§aThere are no eliminated players!");
            return CommandExecutionResult.HANDLED;
        }

        final var reviver = sender instanceof Player player ? player : null;

        int revivedCount = 0;

        for (var entry : all) {
            final var player = Helix.players().getOffline(entry);
            final var revived = this.eliminationManager.tryRevive(player, reviver);

            if (revived) {
                revivedCount++;
            }
        }

        if (revivedCount == 0) {
            sender.sendMessage("§aNo players could be revived!");
            return CommandExecutionResult.HANDLED;
        }

        sender.sendMessage("§aRevived §3" + reviver + " §aplayer" + (revivedCount != 1 ? "s" : ""));

        return CommandExecutionResult.HANDLED;
    }
}
