package me.ikevoodoo.lssmp.commands.health;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.OptionalFor;
import org.bukkit.entity.Player;

public class HealthSubCommand extends SMPCommand {
    protected HealthSubCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.HealthCommand.HealthSubCommand.name, CommandConfig.HealthCommand.HealthSubCommand.perms);
        setArgs(
                new Argument("player", true, Player.class, OptionalFor.NONE),
                new Argument("hearts", true, Double.class, OptionalFor.NONE)
        );
    }

    @Override
    public boolean execute(Context<?> context) {
        var hearts = context.args().get("hearts", Double.class);
        var health = hearts * MainConfig.Elimination.getHeartScale();

        var player = context.args().get("player", Player.class);

        var oldHearts = getPlugin().getHealthHelper().getMaxHealth(player) / MainConfig.Elimination.getHeartScale();

        var newHearts = getPlugin().getHealthHelper().decreaseMaxHealth(player, health) / MainConfig.Elimination.getHeartScale();
        context.source().sendMessage(String.format(
                CommandConfig.HealthCommand.Messages.subMessage,
                player.getName(),
                hearts,
                oldHearts,
                newHearts
        ));
        return true;
    }
}
