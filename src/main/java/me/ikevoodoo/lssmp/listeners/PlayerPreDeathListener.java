package me.ikevoodoo.lssmp.listeners;

import dev.refinedtech.configlang.scope.Scope;
import me.ikevoodoo.lssmp.LSSMP;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.lssmp.language.YamlConfigSection;
import me.ikevoodoo.lssmp.utils.Util;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.events.PlayerPreDeathEvent;
import me.ikevoodoo.smpcore.events.TotemCheckEvent;
import me.ikevoodoo.smpcore.listeners.SMPListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Objects;
import java.util.UUID;

public class PlayerPreDeathListener extends SMPListener {

    public PlayerPreDeathListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerPreKill(PlayerPreDeathEvent event) {
        var elimination = getConfig(MainConfig.class).getEliminationConfig();
        var killed = event.getPlayer();
        var world = killed.getWorld();
        var eventKiller = event.getKiller();

        if(!elimination.isWorldAllowed(world)) {
            return;
        }

        if (!(eventKiller instanceof Player killer)) {
            return;
        }

        fireKillEvent(killed, killer);

        if (!elimination.allowSelfElimination() && Objects.equals(killed, killer)) {
            return;
        }

        Util.increaseOrDrop(
                elimination.getHeartScale(),
                elimination.getMax(),
                killer,
                killed.getEyeLocation(),
                getPlugin()
        );

        var result = getPlugin().getHealthHelper().decreaseMaxHealthIfOver(
                killed,
                elimination.getHeartScale(),
                elimination.getMinHearts()
        );

        if(result.newHealth() <= elimination.getMinHearts() && elimination.banAtMinHealth())
            eliminate(killed);
    }

    @EventHandler
    public void onEnvironmentPreKill(PlayerPreDeathEvent event) {
        var elimination = getConfig(MainConfig.class).getEliminationConfig();
        var player = event.getPlayer();
        var world = player.getWorld();

        if(!elimination.isWorldAllowed(world)) {
            return;
        }

        var killer = event.getKiller();

        if (killer instanceof Player) {
            return;
        }

        if (!elimination.environmentStealsHearts()) {
            return;
        }

        fireKillEvent(player, killer);

        if (!elimination.allowSelfElimination() && Objects.equals(player, killer)) {
            return;
        }

        if(elimination.alwaysDropHearts() || elimination.environmentDropHearts()) {
            Util.drop(
                    getPlugin()
                            .getItem("heart_item")
                            .orElseThrow()
                            .getItemStack(),
                    player.getEyeLocation()
            );
        }

        var setResult = getPlugin().getHealthHelper().decreaseMaxHealthIfOver(
                player,
                elimination.getEnvironmentHeartScale(),
                elimination.getMinHearts()
        );

        if(setResult.newHealth() <= elimination.getMinHearts() && elimination.banAtMinHealth())
            eliminate(player);
    }

    @EventHandler
    public void on(TotemCheckEvent event) {
        if (getConfig(MainConfig.class).getEliminationConfig().totemWorksInInventory()) {
            event.setHasTotem(event.getInventory().contains(Material.TOTEM_OF_UNDYING));
        }
    }

    private void eliminate(Player player) {
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> Util.eliminate(getPlugin(), player), 1);
    }

    private void fireKillEvent(Player killed, Entity killer) {
        Scope scope = new Scope("elimination");

        scope.variables().set("hasVictim", killed != null);
        scope.variables().set("hasKiller", killer != null);
        scope.variables().set("hasPlayerKiller", killer instanceof Player);

        if (killed != null) {
            var killedWorld = killed.getWorld();
            scope.variables().set("killed", new Object() {
                public final String name = killed.getName();
                public final String displayName = killed.getDisplayName();
                public final UUID uuid = killed.getUniqueId();
                public final double health = killed.getHealth();
                public final double maxHealth = killed.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

                public final String worldName = killedWorld.getName();
                public final UUID worldUUID = killedWorld.getUID();
            });
        }

        if (killer != null) {
            var killerWorld = killer.getWorld();
            scope.variables().set("killer", new Object() {
                public final String name = killer.getName();
                public final String displayName = killer instanceof Player player ? player.getDisplayName() : killer.getName();
                public final UUID uuid = killer.getUniqueId();
                public final boolean isMob = killer instanceof LivingEntity;
                public final double health = killer instanceof LivingEntity livingEntity ? livingEntity.getHealth() : 0;
                public final double maxHealth = killer instanceof LivingEntity livingEntity
                        ? livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()
                        : 0;

                public final String worldName = killerWorld.getName();
                public final UUID worldUUID = killerWorld.getUID();
            });
        }


        getPlugin(LSSMP.class).getLanguage().execute(YamlConfigSection.of(
                getYmlConfig("events.yml").getConfigurationSection("killed")), scope);
    }

}
