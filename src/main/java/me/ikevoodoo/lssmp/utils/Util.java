package me.ikevoodoo.lssmp.utils;

import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.items.ItemEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
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

}
