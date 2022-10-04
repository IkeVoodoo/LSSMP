package me.ikevoodoo.lssmp.commands.withdraw;

import me.ikevoodoo.lssmp.LSSMP;
import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.lssmp.utils.Util;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.CommandUsable;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.OptionalFor;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WithdrawCommand extends SMPCommand {
    public WithdrawCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.WithdrawCommand.name, CommandConfig.WithdrawCommand.perms);
        setUsable(CommandUsable.PLAYER);
        setArgs(new Argument("amount", false, Integer.class, OptionalFor.ALL));
    }

    @Override
    public boolean execute(Context<?> context) {
        Player player = context.source(Player.class);
        int amount = Math.abs(context.args().get("amount", Integer.class, 1));
        double max = HealthUtils.get(player);
        if (amount > max) {
            player.sendMessage(CommandConfig.WithdrawCommand.Messages.withdrawnTooMuch.replace("%max%", String.valueOf(max)));
            return true;
        }
        HealthUtils.SetResult result = HealthUtils.decreaseIfOver(
                MainConfig.Elimination.environmentHealthScale * 2 * amount,
                0,
                player,
            getPlugin()
        );

        if(result.isWithin() || result.hasUsedDefault()) {
            int sum = player.getInventory()
                            .addItem(getPlugin(LSSMP.class).getItem("heart_item").orElseThrow()
                                                           .getItemStack(amount))
                            .values()
                            .stream()
                            .map(ItemStack::getAmount)
                            .reduce(0, Integer::sum);

            if (!MainConfig.Elimination.fullInventoryHeartDrop && sum > 0) {
                HealthUtils.increase(sum * 2D, player, getPlugin());
                player.sendMessage(CommandConfig.WithdrawCommand.Messages.notEnoughSpace.replace("%amount%", String.valueOf(sum)));
                return true;
            }

            while (sum > 0) {
                int count = Math.min(sum, 64);

                Util.drop(
                    getPlugin()
                        .getItem("heart_item")
                        .orElseThrow()
                        .getItemStack(count),
                    player.getEyeLocation()
                );

                sum -= count;
            }
        }
        else {
            getPlugin().getEliminationHandler().eliminate(player);
            player.kickPlayer(MainConfig.Elimination.Bans.banMessage);
            return true;
        }
        player.sendMessage(CommandConfig.WithdrawCommand.Messages.withdraw.replace("%amount%", String.valueOf(amount)));
        return true;
    }
}