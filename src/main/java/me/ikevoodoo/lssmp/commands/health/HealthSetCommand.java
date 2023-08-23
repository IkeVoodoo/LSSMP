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

import java.util.Map;

public class HealthSetCommand extends SMPCommand {
    protected HealthSetCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getHealthCommand().getHealthSetCommand().name(),
                "permission", commandConfig.getHealthCommand().getHealthSetCommand().perms()
        )));

        setArgs(
                new Argument("player", true, Player.class, OptionalFor.NONE),
                new Argument("health", true, Double.class, OptionalFor.NONE),
                new Argument("world", false, World.class, OptionalFor.ALL)
        );
    }

    @Override
    public boolean execute(Context<?> context) {
        var heartScale = getConfig(MainConfig.class).getEliminationConfig().getHeartScale();
        var messages = getConfig(CommandConfig.class).getHealthCommand().getMessages();

        var hearts = context.args().get("health", Double.class);
        var health = hearts * heartScale;
        var player = context.args().get("player", Player.class);
        var oldHearts = getPlugin().getHealthHelper().getMaxHealth(player) / heartScale;

        if (context.args().has("world")) {
            var world = context.args().get("world", World.class);
            if (world == null) {
                context.source().sendMessage(String.format(
                        messages.unknownWorld(),
                        context.args().get("world", String.class)
                ));
                return true;
            }

            var newHearts = getPlugin().getHealthHelper().setMaxHealth(player, health, world) / heartScale;
            getPlugin().getHealthHelper().updateHealth(player);

            context.source().sendMessage(String.format(
                    messages.setInWorldMessage(),
                    player.getName(),
                    newHearts,
                    world.getName(),
                    oldHearts
            ));
            return true;
        }

        getPlugin().getHealthHelper().setMaxHealthEverywhere(player, health);
        getPlugin().getHealthHelper().updateHealth(player);



        context.source().sendMessage(String.format(
                messages.setMessage(),
                player.getName(),
                hearts,
                oldHearts
        ));
        return true;
    }
}
