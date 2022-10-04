package me.ikevoodoo.lssmp.commands.health;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.OptionalFor;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import org.bukkit.entity.Player;

public class HealthAddCommand extends SMPCommand {
    protected HealthAddCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.HealthCommand.HealthAddCommand.name, CommandConfig.HealthCommand.HealthAddCommand.perms);
        setArgs(
                new Argument("player", true, Player.class, OptionalFor.NONE),
                new Argument("hearts", true, Double.class, OptionalFor.NONE)
        );
    }

    @Override
    public boolean execute(Context<?> context) {
        double hearts = context.args().get("hearts", Double.class);
        Player player = context.args().get("player", Player.class);
        HealthUtils.increase(hearts * 2, player, getPlugin());
        return true;
    }
}
