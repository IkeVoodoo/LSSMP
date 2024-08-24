package me.ikevoodoo.lssmp.listeners;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.events.player.PlayerKilledEvent;
import me.ikevoodoo.lssmp.elimination.EliminationHelper;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipeline;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.atomic.AtomicBoolean;

public class LifestealCombatListener implements Listener {

    private final HeartPipeline pipeline;

    public LifestealCombatListener(HeartPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKilled(PlayerKilledEvent event) {
        var player = event.getPlayer();
        var attacker = event.getKiller();

        var killedAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert killedAttribute != null;

        var attackerAttribute = attacker == null ? null : attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        var playerHearts = new AtomicDouble(killedAttribute.getBaseValue());
        AtomicDouble attackerHearts;
        if (attacker == null) {
            attackerHearts = null;
        } else {
            assert attackerAttribute != null;
            attackerHearts = new AtomicDouble(attackerAttribute.getBaseValue());
        }

        var cancel = new AtomicBoolean(false);

        this.pipeline.fire(player, attacker, playerHearts, attackerHearts, cancel);

        if (cancel.get()) {
            if (playerHearts.get() < 0) {
                EliminationHelper.eliminate(player, attacker);
            }

            event.setCancelled(true);
            return;
        }

        killedAttribute.setBaseValue(playerHearts.doubleValue());

        if (attackerAttribute != null) {
            attackerAttribute.setBaseValue(attackerHearts.doubleValue());
        }
    }
}
