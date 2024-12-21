package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.lssmp.elimination.EliminationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EliminateAllCommand extends HelixCommand {

    private final EliminationManager eliminationManager;

    public EliminateAllCommand(EliminationManager eliminationManager) {
        this.eliminationManager = eliminationManager;
    }

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create("all");
    }

    @Override
    public CommandExecutionResult handleGenericSender(@NotNull CommandSender sender, @NotNull ArgumentList args) {
        var online = Helix.players().allOnline();
        if (online.isEmpty()) {
            sender.sendMessage("§aThere are no players!");
            return CommandExecutionResult.HANDLED;
        }

        final var senderPlayer = sender instanceof Player player ? player : null;

        int eliminatedCount = 0;

        for (var plr : online) {
            final var result = this.eliminationManager.tryEliminate(plr, senderPlayer);
            switch (result) {
                case ELIMINATION, ATTEMPTED_ELIMINATION -> eliminatedCount++;
            }
        }

        sender.sendMessage("§aEliminated §3" + eliminatedCount + " §aplayer" + (eliminatedCount != 1 ? "s" : ""));

        return CommandExecutionResult.HANDLED;
    }
}
