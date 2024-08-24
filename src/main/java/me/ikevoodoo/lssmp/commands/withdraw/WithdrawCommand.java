package me.ikevoodoo.lssmp.commands.withdraw;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.lssmp.elimination.EliminationHelper;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipeline;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class WithdrawCommand extends HelixCommand {

    private final Configuration configuration;
    private final HeartPipeline pipeline;

    public WithdrawCommand(Configuration configuration, HeartPipeline pipeline) {
        this.configuration = configuration;
        this.pipeline = pipeline;
    }

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create(this.configuration.getValue("name"))
                .permission(this.configuration.getValue("permission"));
    }

    @Override
    public CommandExecutionResult handlePlayerSender(@NotNull Player player, @NotNull ArgumentList args) {
        var hearts = new AtomicDouble(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
        var cancel = new AtomicBoolean();
        this.pipeline.fire(player, null, hearts, null, cancel);

        if (cancel.get()) {
            if (hearts.get() < 0 && this.configuration.<Boolean>getValue("allowSelfElimination")) {
                EliminationHelper.eliminate(player, player);
                return CommandExecutionResult.FAILURE;
            }

            player.sendMessage("§cYou can't eliminate yourself!");
            return CommandExecutionResult.FAILURE;
        }

        var stack = Helix.items().createItem(this.configuration.getValue("heartItem"), null);
        var res = player.getInventory().addItem(stack);
        for (var result : res.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), result);
        }

        player.sendMessage("§aSuccessfully withdrawn a heart!");
        return CommandExecutionResult.HANDLED;
    }
}
