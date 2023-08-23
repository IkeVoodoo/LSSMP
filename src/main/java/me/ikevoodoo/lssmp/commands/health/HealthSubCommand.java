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

import java.util.Map;

public class HealthSubCommand extends SMPCommand {
    protected HealthSubCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getHealthCommand().getHealthSubCommand().name(),
                "permission", commandConfig.getHealthCommand().getHealthSubCommand().perms()
        )));
        setArgs(
                new Argument("player", true, Player.class, OptionalFor.NONE),
                new Argument("hearts", true, Double.class, OptionalFor.NONE),
                new Argument("world", false, World.class, OptionalFor.ALL)
        );
    }

    @Override
    public boolean execute(Context<?> context) {
        var heartScale = getConfig(MainConfig.class).getEliminationConfig().getHeartScale();
        var messages = getConfig(CommandConfig.class).getHealthCommand().getMessages();

        var hearts = context.args().get("hearts", Double.class);
        var health = hearts * heartScale;
        var player = context.args().get("player", Player.class);

        World world = null;

        if (context.args().has("world")) {
            world = context.args().get("world", World.class);
            if (world == null) {
                context.source().sendMessage(String.format(
                        messages.unknownWorld(),
                        context.args().get("world", String.class)
                ));
                return true;
            }
        }

        var oldHearts = getPlugin().getHealthHelper().getMaxHealth(player, world) / heartScale;
        var newHearts = decreaseMaxHealth(player, health, world, false) / heartScale;

        getPlugin().getHealthHelper().updateHealth(player);

        context.source().sendMessage(String.format(
                messages.subMessage(),
                player.getName(),
                hearts,
                oldHearts,
                newHearts
        ));
        return true;
    }

    private double decreaseMaxHealth(Player player, double health, @Nullable World world, boolean useLimit) {
        if (useLimit) {
            return getPlugin().getHealthHelper().decreaseMaxHealthIfOver(
                    player,
                    health,
                    getConfig(MainConfig.class).getEliminationConfig().getMinHealth(),
                    world
            ).newHealth();
        }

        return getPlugin().getHealthHelper().decreaseMaxHealth(player, health, world);
    }
}
