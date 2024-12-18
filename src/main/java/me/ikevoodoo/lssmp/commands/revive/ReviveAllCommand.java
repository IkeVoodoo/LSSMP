package me.ikevoodoo.lssmp.commands.revive;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.lssmp.elimination.EliminationHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReviveAllCommand extends HelixCommand {

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

        var amount = all.size();

        final var reviver = sender instanceof Player player ? player : null;

        for (var entry : all) {
            EliminationHelper.revive(Helix.players().getOffline(entry), reviver);
        }

        sender.sendMessage("§aRevived §3" + amount + " §aplayer" + (amount != 1 ? "s" : ""));

        return CommandExecutionResult.HANDLED;
    }
}
