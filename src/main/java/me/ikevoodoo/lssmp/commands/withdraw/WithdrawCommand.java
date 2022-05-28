package me.ikevoodoo.lssmp.commands.withdraw;

import me.ikevoodoo.lssmp.LSSMP;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.CommandUsable;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WithdrawCommand extends SMPCommand {
    public WithdrawCommand(SMPPlugin plugin) {
        super(plugin, "lswithdraw", "lssmp.withdraw");
        setUsable(CommandUsable.PLAYER);
    }

    @Override
    public boolean execute(CommandSender commandSender, Arguments arguments) {
        Player player = (Player) commandSender;
        if(HealthUtils.decreaseIfOver(2, 0, player))
            CustomItem.give(player, getPlugin(LSSMP.class).getItem("heart_item").orElseThrow());
        else
            getPlugin().getEliminationHandler().eliminate(player);
        commandSender.sendMessage("§6You have withdrawn a heart!");
        return true;
    }
}