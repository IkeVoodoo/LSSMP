package me.ikevoodoo.lssmp.listeners;

import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.lssmp.config.bans.BanConfig;
import me.ikevoodoo.lssmp.utils.Util;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.events.PlayerPreDeathEvent;
import me.ikevoodoo.smpcore.events.TotemCheckEvent;
import me.ikevoodoo.smpcore.handlers.EliminationData;
import me.ikevoodoo.smpcore.listeners.SMPListener;
import me.ikevoodoo.smpcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Objects;

public class PlayerPreDeathListener extends SMPListener {

    public PlayerPreDeathListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerPreKill(PlayerPreDeathEvent event) {
        var killed = event.getPlayer();
        var world = killed.getWorld();
        var eventKiller = event.getKiller();

        if (!(eventKiller instanceof Player killer)) {
            return;
        }

        if (!MainConfig.Elimination.allowSelfElimination && Objects.equals(killed, killer)) {
            return;
        }

        if(!MainConfig.Elimination.isWorldAllowed(world)) {
            return;
        }

        Bukkit.broadcastMessage("[PPK] Passed checks!");

        Util.increaseOrDrop(
                MainConfig.Elimination.getHeartScale(),
                MainConfig.Elimination.getMax(),
                killer,
                killed.getEyeLocation(),
                getPlugin()
        );

        var result = getPlugin().getHealthHelper().decreaseMaxHealthIfOver(
                killed,
                MainConfig.Elimination.getHeartScale(),
                MainConfig.Elimination.getMinHearts()
        );

        if(result.newHealth() <= MainConfig.Elimination.getMinHearts() && MainConfig.Elimination.banAtMinHealth)
            eliminate(killed);

        Bukkit.broadcastMessage("[PPK] Processed!");
    }

    @EventHandler
    public void onEnvironmentPreKill(PlayerPreDeathEvent event) {
        var player = event.getPlayer();
        var world = player.getWorld();
        var killer = event.getKiller();

        if (killer instanceof Player) {
            return;
        }

        if (!MainConfig.Elimination.environmentStealsHearts) {
            return;
        }

        if (!MainConfig.Elimination.allowSelfElimination && Objects.equals(player, killer)) {
            return;
        }

        if(!MainConfig.Elimination.isWorldAllowed(world)) {
            return;
        }

        Bukkit.broadcastMessage("[EPK] Passed checks!");

        if(MainConfig.Elimination.alwaysDropHearts || MainConfig.Elimination.environmentDropHearts) {
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
                MainConfig.Elimination.getEnvironmentHeartScale(),
                MainConfig.Elimination.getMinHearts()
        );

        if(setResult.newHealth() <= MainConfig.Elimination.getMinHearts() && MainConfig.Elimination.banAtMinHealth)
            eliminate(player);

        Bukkit.broadcastMessage("[EPK] Processed!");
    }

    @EventHandler
    public void on(TotemCheckEvent event) {
        if (MainConfig.Elimination.totemWorksInInventory) {
            event.setHasTotem(event.getInventory().contains(Material.TOTEM_OF_UNDYING));
        }
    }

    private void eliminate(Player player) {
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (getPlugin().getEliminationHandler().isEliminated(player)) return;

            var banData = BanConfig.INSTANCE.findHighest(player);

            var standardBanTime = StringUtils.parseBanTime(MainConfig.Elimination.Bans.banTime);
            var standardBanMessage = MainConfig.Elimination.Bans.banMessage;

            var banTime = banData == null ? standardBanTime : banData.time();
            var banMessage = banData == null ? standardBanMessage : banData.banMessage();

            if(MainConfig.Elimination.Bans.broadcastBan) {
                var standardBroadcastMessage = MainConfig.Elimination.Bans.broadcastMessage;

                var broadcastMessage = banData == null ? standardBroadcastMessage : banData.broadcastBanMessage();

                Bukkit.broadcastMessage(broadcastMessage.replace("%player%", player.getDisplayName()));
            }

            var usedTime = MainConfig.Elimination.Bans.useBanTime ? banTime : Long.MAX_VALUE;

            getPlugin().getEliminationHandler().eliminate(player, new EliminationData(banMessage, usedTime));

            player.kickPlayer(MainConfig.Elimination.Bans.banMessage);
        }, 1);
    }

}
