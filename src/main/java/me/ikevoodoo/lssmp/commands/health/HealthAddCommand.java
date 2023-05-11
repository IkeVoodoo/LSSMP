package me.ikevoodoo.lssmp.commands.health;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.OptionalFor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class HealthAddCommand extends SMPCommand {
    protected HealthAddCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.HealthCommand.HealthAddCommand.name, CommandConfig.HealthCommand.HealthAddCommand.perms);
        setArgs(
                new Argument("player", true, Player.class, OptionalFor.NONE),
                new Argument("hearts", true, Double.class, OptionalFor.NONE),
                new Argument("world", false, World.class, OptionalFor.ALL)
        );
    }

    @Override
    public boolean execute(Context<?> context) {
        var hearts = context.args().get("hearts", Double.class);
        var health = hearts * MainConfig.Elimination.getHeartScale();
        var player = context.args().get("player", Player.class);

        World world = null;

        if (context.args().has("world")) {
            world = context.args().get("world", World.class);
            if (world == null) {
                context.source().sendMessage(String.format(
                        CommandConfig.HealthCommand.Messages.unknownWorld,
                        context.args().get("world", String.class)
                ));
                return true;
            }
        }

        var oldHearts = getPlugin().getHealthHelper().getMaxHealth(player, world) / MainConfig.Elimination.getHeartScale();
        var newHearts = increaseMaxHealth(player, health, world, false) / MainConfig.Elimination.getHeartScale();

        getPlugin().getHealthHelper().updateHealth(player);

        context.source().sendMessage(String.format(
                CommandConfig.HealthCommand.Messages.addMessage,
                player.getName(),
                hearts,
                oldHearts,
                newHearts
        ));
        return true;
    }

    private double increaseMaxHealth(Player player, double health, @Nullable World world, boolean useLimit) {
        if (useLimit) {
            return getPlugin().getHealthHelper().increaseMaxHealthIfUnder(
                    player,
                    health,
                    MainConfig.Elimination.getMax(),
                    world
            ).newHealth();
        }

        return getPlugin().getHealthHelper().increaseMaxHealth(player, health, world);
    }
}
