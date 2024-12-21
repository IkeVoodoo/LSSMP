package me.ikevoodoo.lssmp.listeners;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.events.player.PlayerKilledEvent;
import me.ikevoodoo.lssmp.elimination.EliminationManager;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipeline;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class LifestealCombatListener implements Listener {

    private final HeartPipeline pipeline;
    private final EliminationManager eliminationManager;

    public LifestealCombatListener(HeartPipeline pipeline, EliminationManager eliminationManager) {
        this.pipeline = pipeline;
        this.eliminationManager = eliminationManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKilled(PlayerKilledEvent event) {
        var victim = event.getPlayer();

        final var deathGrace = Helix.tags().get("player_death_grace");
        if (deathGrace.has(victim.getUniqueId())) {
            return;
        }
        deathGrace.add(victim.getUniqueId(), (uuid, helixDataStorage) -> {});

        var attacker = event.getKiller();

        var victimAttribute = victim.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert victimAttribute != null;

        var attackerAttribute = attacker == null ? null : attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        var victimHearts = new AtomicDouble(victimAttribute.getBaseValue());

        final var attackerHearts = attackerAttribute == null
                ? null
                : new AtomicDouble(attackerAttribute.getBaseValue());

        var cancel = new AtomicBoolean(false);

        this.pipeline.fire(victim, attacker, victimHearts, attackerHearts, cancel);

        if (cancel.get()) {
            if (victimHearts.get() < 0) {
                this.eliminationManager.tryEliminate(victim, attacker);
            }

            event.setCancelled(true);
            return;
        }

        victimAttribute.setBaseValue(victimHearts.doubleValue());

        if (attackerAttribute != null) {
            attackerAttribute.setBaseValue(attackerHearts.doubleValue());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final var deathGrace = Helix.tags().get("player_death_grace");
        deathGrace.remove(event.getPlayer().getUniqueId());
    }


}
