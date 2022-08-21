package me.ikevoodoo.lssmp.commands.withdraw;

import me.ikevoodoo.lssmp.LSSMP;
import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.CommandUsable;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.OptionalFor;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import org.bukkit.entity.Player;

public class WithdrawCommand extends SMPCommand {
    public WithdrawCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.WithdrawCommand.name, CommandConfig.WithdrawCommand.perms);
        setUsable(CommandUsable.PLAYER);
        setArgs(new Argument("amount", false, Integer.class, OptionalFor.ALL));
    }

    @Override
    public boolean execute(Context<?> context) {
        Player player = context.source(Player.class);
        int amount = (int) Math.ceil(Math.abs(context.args().get("amount", Integer.class, 1)));
        HealthUtils.SetResult result = HealthUtils.decreaseIfOver(
                MainConfig.Elimination.environmentHealthScale * 2 * amount,
                0,
                player
        );

        if(result.isWithin() || result.hasUsedDefault())
            player.getInventory().addItem(getPlugin(LSSMP.class).getItem("heart_item").orElseThrow()
                    .getItemStack(amount));
        else {
            getPlugin().getEliminationHandler().eliminate(player);
            player.kickPlayer(MainConfig.Elimination.Bans.banMessage);
            return true;
        }
        player.sendMessage(CommandConfig.WithdrawCommand.Messages.withdraw.replace("%amount%", amount + ""));
        return true;
    }
}