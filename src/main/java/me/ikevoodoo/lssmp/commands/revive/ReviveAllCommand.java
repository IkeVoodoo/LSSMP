package me.ikevoodoo.lssmp.commands.revive;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;

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

        var toRemove = new HashSet<UUID>();

        for (var entry : all) {
            var player = Helix.players().getOnline(entry);

            if (player == null) {
                tag.editData(entry, storage -> storage.setLong("banTime", 0));
                continue;
            }

            toRemove.add(entry);
        }

        for (var entry : toRemove) {
            tag.remove(entry);
        }

        sender.sendMessage("§aRevived §3" + amount + " §aplayer" + (amount != 1 ? "s" : ""));

        return CommandExecutionResult.HANDLED;
    }
}
