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
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Objects;

public class PlayerPreDeathListener extends SMPListener {

    public PlayerPreDeathListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(PlayerPreDeathEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (!MainConfig.Elimination.allowSelfElimination && Objects.equals(player, event.getKiller())) {
            return;
        }

        if(!MainConfig.Elimination.isWorldAllowed(world))
            return;

        var hearts = getPlugin().getHealthHelper().getMaxHearts(player);
        if (hearts <= MainConfig.Elimination.getMin()) {
            if (MainConfig.Elimination.banAtMinHealth) {
                this.eliminate(player);
            }
            return;
        }

        if(event.hasKiller() && event.getKiller() instanceof Player killer) {
            this.handlePlayerKill(player, killer);
            return;
        }

        if (!MainConfig.Elimination.environmentStealsHearts)
            return;

        this.handleEnvironmentKill(player);
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

            if(MainConfig.Elimination.Bans.useBanTime) {
                var data = banData == null ? EliminationData.infiniteTime() : new EliminationData(banMessage, banTime);
                getPlugin().getEliminationHandler().eliminate(player, data);
            } else {
                getPlugin().getEliminationHandler().eliminate(player, EliminationData.infiniteTime(banMessage));
            }

            player.kickPlayer(MainConfig.Elimination.Bans.banMessage);
        }, 1);
    }

    private void handleEnvironmentKill(Player player) {
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
                MainConfig.Elimination.getMin()
        );

        if(setResult.newHealth() <= MainConfig.Elimination.getMin())
            eliminate(player);
    }

    private void handlePlayerKill(Player player, Player killer) {
        Util.increaseOrDrop(
                MainConfig.Elimination.getHeartScale(),
                MainConfig.Elimination.getMax(),
                killer,
                player.getEyeLocation(),
                getPlugin()
        );

        var result = getPlugin().getHealthHelper().decreaseMaxHealthIfOver(
                player,
                MainConfig.Elimination.getHeartScale(),
                MainConfig.Elimination.getMin()
        );

        if(result.newHealth() <= MainConfig.Elimination.getMin())
            eliminate(player);
    }

}
