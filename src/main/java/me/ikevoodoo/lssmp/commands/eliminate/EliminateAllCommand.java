package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.lssmp.elimination.EliminationHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EliminateAllCommand extends HelixCommand {

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create("all");
    }

    @Override
    public CommandExecutionResult handleGenericSender(@NotNull CommandSender sender, @NotNull ArgumentList args) {
        var tag = Helix.tags().get("elimination");
        var online = Helix.players().allOnline();
        if (online.isEmpty()) {
            sender.sendMessage("§aThere are no players!");
            return CommandExecutionResult.HANDLED;
        }

        for (var plr : online) {
            tag.add(plr.getUniqueId(), (uuid, storage) -> {});

            var storage = tag.getData(plr.getUniqueId());
            var data = EliminationHelper.fromStorage(plr.getUniqueId(), storage);

            plr.kickPlayer(data.getKickMessage(plr));
        }

        sender.sendMessage("§aEliminated §3" + online + " §aplayer" + (online.size() != 1 ? "s" : ""));

        return CommandExecutionResult.HANDLED;
    }
}
