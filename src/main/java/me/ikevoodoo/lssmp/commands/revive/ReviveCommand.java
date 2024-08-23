package me.ikevoodoo.lssmp.commands.revive;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.helix.api.commands.parsers.PlayerParser;
import me.ikevoodoo.helix.api.config.Configuration;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReviveCommand extends HelixCommand {

    private final Configuration configuration;

    public ReviveCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create(this.configuration.getValue("name"))
                .childCommand(new ReviveAllCommand())
                .permission(this.configuration.getValue("permission"))
                .argument("player", PlayerParser.ALL);
    }

    @Override
    public CommandExecutionResult handleGenericSender(@NotNull CommandSender sender, @NotNull ArgumentList args) {
        var resetting = args.<OfflinePlayer>getArgument("player");

        var tag = Helix.tags().get("elimination");
        if(!tag.has(resetting.getUniqueId())) {
            sender.sendMessage("§aGood news! That player is not eliminated!");
            return CommandExecutionResult.HANDLED;
        }

        if (!resetting.isOnline()) {
            tag.editData(resetting.getUniqueId(), storage -> storage.setLong("banTime", 0));
        } else {
            tag.remove(resetting.getUniqueId());
        }

        sender.sendMessage("§aRevived §3" + resetting.getName());

        return CommandExecutionResult.HANDLED;
    }
}
