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

public class HealthGetCommand extends SMPCommand {
    protected HealthGetCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.HealthCommand.HealthGetCommand.name, CommandConfig.HealthCommand.HealthGetCommand.perms);
        setArgs(
                new Argument("player", true, Player.class, OptionalFor.NONE),
                new Argument("world", false, World.class, OptionalFor.ALL)
        );
    }

    @Override
    public boolean execute(Context<?> context) {
        var player = context.args().get("player", Player.class);
        double hearts;

        if (context.args().has("world")) {
            var world = context.args().get("world", World.class);
            if (world == null) {
                context.source().sendMessage(String.format(
                        CommandConfig.HealthCommand.Messages.unknownWorld,
                        context.args().get("world", String.class)
                ));
                return true;
            }

            hearts = getPlugin().getHealthHelper().getMaxHearts(player, world);
        } else {
            hearts = getPlugin().getHealthHelper().getMaxHearts(player);
        }

        hearts /= MainConfig.Elimination.getHeartScale();

        getPlugin().getHealthHelper().updateHealth(player);

        context.source().sendMessage(String.format(
                CommandConfig.HealthCommand.Messages.getMessage,
                player.getName(),
                hearts
        ));
        return true;
    }
}
