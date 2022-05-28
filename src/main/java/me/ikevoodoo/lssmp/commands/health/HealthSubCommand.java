package me.ikevoodoo.lssmp.commands.health;

import me.ikevoodoo.lssmp.config.ConfigFile;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import me.ikevoodoo.smpcore.commands.arguments.OptionalFor;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealthSubCommand extends SMPCommand {
    protected HealthSubCommand(SMPPlugin plugin) {
        super(plugin, "sub");
        setArgs(
                new Argument("player", true, Player.class, OptionalFor.NONE),
                new Argument("health", true, Double.class, OptionalFor.NONE)
        );
    }

    @Override
    public boolean execute(CommandSender commandSender, Arguments arguments) {
        double health = arguments.get("health", Double.class);
        Player player =arguments.get("player", Player.class);
        HealthUtils.decrease(health, player);
        return true;
    }
}
