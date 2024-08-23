package me.ikevoodoo.lssmp.feature.heart;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.lssmp.configuration.data.types.TotemUseMode;
import me.ikevoodoo.lssmp.pipeline.PipelineResult;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipelineHandler;
import me.ikevoodoo.helix.api.config.Configuration;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class BasicTotemCheck implements HeartPipelineHandler {

    private final Configuration combatConfig;

    public BasicTotemCheck(Configuration combatConfig) {
        this.combatConfig = combatConfig;
    }

    @Override
    public PipelineResult handle(Player player, @Nullable Entity attacker, AtomicDouble playerHearts, @Nullable AtomicDouble attackerHearts, AtomicBoolean cancel) {
        var mode = this.combatConfig.<TotemUseMode>getValue("totemUseMode");

        var hasInHand = hasTotemInHand(player);

        var keepExecuting = switch (mode) {
            case ALWAYS -> player.getInventory().contains(Material.TOTEM_OF_UNDYING);
            case IN_HAND_ONLY -> hasInHand;
            case NEVER -> false;
        };

        if (keepExecuting && mode == TotemUseMode.ALWAYS && !hasInHand) {
            var stacks = player.getInventory().all(Material.TOTEM_OF_UNDYING);

            for (var stack : stacks.values()) {
                stack.setAmount(stack.getAmount() - 1);
                player.updateInventory();
                break;
            }

            player.setHealth(2);
            player.playEffect(EntityEffect.TOTEM_RESURRECT);

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.REGENERATION,
                    45 * 20,
                    1
            ));

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.FIRE_RESISTANCE,
                    40 * 20,
                    0
            ));

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.ABSORPTION,
                    5 * 20,
                    1
            ));

            cancel.set(true);

            return PipelineResult.CANCEL;
        }

        return PipelineResult.fromBoolean(!keepExecuting);
    }

    private boolean hasTotemInHand(Player player) {
        var inventory = player.getInventory();
        var mainHand = inventory.getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING;
        var offHand = inventory.getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING;

        return mainHand || offHand;
    }
}
