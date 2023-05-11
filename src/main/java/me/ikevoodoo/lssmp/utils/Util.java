package me.ikevoodoo.lssmp.utils;

import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.lssmp.config.bans.BanConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.handlers.EliminationData;
import me.ikevoodoo.smpcore.items.ItemEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Util {

    private Util() {

    }

    public static void increaseOrDrop(double amount, double max, LivingEntity entity, Location dropAt, SMPPlugin plugin) {
        if (MainConfig.Elimination.alwaysDropHearts || MainConfig.Elimination.playersDropHearts) {
            drop(plugin
                    .getItem("heart_item")
                    .orElseThrow()
                    .getItemStack(), dropAt);
            Bukkit.broadcastMessage("Dropping heart.");
            return;
        }

        var result = plugin.getHealthHelper().increaseMaxHealthIfUnder(
                entity,
                amount,
                max
        );

        Bukkit.broadcastMessage(result.toString());

        if (!result.isInRange()) {
            drop(plugin
                    .getItem("heart_item")
                    .orElseThrow()
                    .getItemStack(), dropAt);
        }
    }

    public static void drop(ItemStack stack, Location loc) {
        ItemEntity.of(stack)
                .setGravity(false)
                .setGlowing(true)
                .setInvulnerable(true)
                .setLocation(loc)
                .noVelocity()
                .spawn();
    }

    public static void eliminate(SMPPlugin plugin, Player player) {
        if (plugin.getEliminationHandler().isEliminated(player)) return;

        var banData = BanConfig.INSTANCE.findHighest(player);
        if (banData == null) {
            if (MainConfig.Elimination.Bans.broadcastBan) {
                var banMessage = MainConfig.Elimination.Bans.broadcastMessage;

                Bukkit.broadcastMessage(banMessage.replace("%player%", player.getDisplayName()));
            }

            var banTime = MainConfig.Elimination.Bans.getBanTime();
            var banMessage = MainConfig.Elimination.Bans.banMessage;
            plugin.getEliminationHandler().eliminate(player, new EliminationData(banMessage, banTime));
            player.kickPlayer(banMessage);
            return;
        }

        if (banData.broadcastBan()) {
            var broadcastMessage = banData.broadcastBanMessage();
            var coloredBroadcastMessage = ChatColor.translateAlternateColorCodes('&', broadcastMessage);

            Bukkit.broadcastMessage(coloredBroadcastMessage.replace("%player%", player.getDisplayName()));
        }

        var banMessage = banData.banMessage();
        var coloredBanMessage = ChatColor.translateAlternateColorCodes('&', banMessage);

        plugin.getEliminationHandler().eliminate(player, new EliminationData(coloredBanMessage, banData.time()));
        player.kickPlayer(coloredBanMessage);
    }

}
