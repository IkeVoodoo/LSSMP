package me.ikevoodoo.lssmp.commands.setup;

import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.helix.api.messages.MessageBuilder;
import me.ikevoodoo.helix.api.messages.colors.MinecraftColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class SetupCommand extends HelixCommand {

    private final Configuration commandConfig;
    private final Configuration generalConfig;
    private final Map<UUID, BiConsumer<Player, String>> messageConsumer;

    public SetupCommand(Configuration commandConfig, Configuration generalConfig, Map<UUID, BiConsumer<Player, String>> messageConsumer) {
        this.commandConfig = commandConfig;
        this.generalConfig = generalConfig;
        this.messageConsumer = messageConsumer;
    }

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create("lssetup")
                .permission("lssmp.setup")
                .childCommand(new SetupConfigCommand(this.messageConsumer, this.commandConfig, this.generalConfig));
    }

    @Override
    public CommandExecutionResult handlePlayerSender(@NotNull Player player, @NotNull ArgumentList args) {
        var msg = new MessageBuilder()
                .literal("Setup will take roughly 5 minutes. Confirm to continue. ")
                .color(MinecraftColor.GREEN)

                .literal("[CONFIRM]")
                .bold(true)
                .color(MinecraftColor.YELLOW)
                .click(ClickEvent.Action.RUN_COMMAND, "/lssetup config /init")
                .hover(HoverEvent.Action.SHOW_TEXT, new Text("/lssetup config /init"));

        player.spigot().sendMessage(msg.build());

        return CommandExecutionResult.HANDLED;
    }
}
