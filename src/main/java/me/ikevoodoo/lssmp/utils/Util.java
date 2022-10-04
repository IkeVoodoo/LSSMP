package me.ikevoodoo.lssmp.utils;

import me.ikevoodoo.lssmp.LSSMP;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.items.ItemEntity;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class Util {

    private static final LSSMP PLUGIN = SMPPlugin.getPlugin(LSSMP.class);

    private Util() {

    }

    public static void increaseOrDrop(double amount, double max, LivingEntity entity, Location dropAt, SMPPlugin plugin) {
        if (MainConfig.Elimination.alwaysDropHearts) {
            drop(PLUGIN
                    .getItem("heart_item")
                    .orElseThrow()
                    .getItemStack(), dropAt);
            return;
        }

        HealthUtils.SetResult result =
                HealthUtils.increaseIfUnder(
                        amount,
                        max,
                        entity,
                        true,
                    plugin
                );

        if (result.isOutOfBounds()) {
            drop(PLUGIN
                    .getItem("heart_item")
                    .orElseThrow()
                    .getItemStack(), dropAt);
        }
    }

    public static void drop(ItemStack stack, Location loc) {
        ItemEntity.of(stack)
                .setGravity(false)
                .setGlowing(true)
                .setLocation(loc)
                .noVelocity()
                .spawn();
    }

}
