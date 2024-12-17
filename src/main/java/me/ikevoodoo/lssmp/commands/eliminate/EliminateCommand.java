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
                .argument("player", PlayerParser.ALL);
    }

    @Override
    public CommandExecutionResult handleGenericSender(@NotNull CommandSender sender, @NotNull ArgumentList args) {
        var resetting = args.<OfflinePlayer>getArgument("player");

        var tag = Helix.tags().get("elimination");
        if(tag.has(resetting.getUniqueId())) {
            sender.sendMessage("§aGood news! That player is already eliminated!");
            return CommandExecutionResult.HANDLED;
        }

        sender.sendMessage("§aEliminated §3" + resetting.getName());

        tag.add(resetting.getUniqueId(), (uuid, storage) -> {});

        if (resetting.isOnline()) {
            var player = resetting.getPlayer();
            assert player != null;

            var storage = tag.getData(player.getUniqueId());
            var data = EliminationHelper.fromStorage(player.getUniqueId(), storage);

            player.kickPlayer(data.getKickMessage(player));
        }

        return CommandExecutionResult.HANDLED;
    }
}
