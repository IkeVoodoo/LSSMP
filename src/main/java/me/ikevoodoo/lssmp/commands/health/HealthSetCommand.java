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

public class HealthSetCommand extends SMPCommand {
    protected HealthSetCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.HealthCommand.HealthSetCommand.name, CommandConfig.HealthCommand.HealthSetCommand.perms);

        setArgs(
                new Argument("player", true, Player.class, OptionalFor.NONE),
                new Argument("hearts", true, Double.class, OptionalFor.NONE),
                new Argument("world", false, World.class, OptionalFor.ALL)
        );
    }

    @Override
    public boolean execute(Context<?> context) {
        var hearts = context.args().get("hearts", Double.class) * MainConfig.Elimination.getHeartScale();
        var player = context.args().get("player", Player.class);
        var oldHearts = getPlugin().getHealthHelper().getMaxHealth(player) / MainConfig.Elimination.getHeartScale();

        if (context.args().has("world")) {
            var world = context.args().get("world", World.class);

            var setTo = getPlugin().getHealthHelper().setMaxHealth(player, hearts, world);

            context.source().sendMessage(String.format(
                    CommandConfig.HealthCommand.Messages.setInWorldMessage,
                    player.getName(),
                    setTo,
                    world.getName(),
                    oldHearts
            ));
            return true;
        }

        getPlugin().getHealthHelper().setMaxHealthEverywhere(player, hearts);
        context.source().sendMessage(String.format(
                CommandConfig.HealthCommand.Messages.setMessage,
                player.getName(),
                hearts,
                oldHearts
        ));
        return true;
    }
}
