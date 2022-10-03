package me.ikevoodoo.lssmp.listeners;

import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.lssmp.utils.Util;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.events.PlayerPreDeathEvent;
import me.ikevoodoo.smpcore.events.TotemCheckEvent;
import me.ikevoodoo.smpcore.listeners.SMPListener;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import me.ikevoodoo.smpcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class PlayerPreDeathListener extends SMPListener {

    public PlayerPreDeathListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(PlayerPreDeathEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if(!MainConfig.Elimination.isWorldAllowed(world))
            return;

        if(event.hasKiller() && event.getKiller() instanceof Player killer && (!MainConfig.Elimination.useMinHealth || HealthUtils.get(player) > MainConfig.Elimination.getMin())) {
            Util.increaseOrDrop(
                    MainConfig.Elimination.healthScale * 2,
                    MainConfig.Elimination.getMax(),
                    killer,
                    player.getEyeLocation()
            );

            HealthUtils.decreaseIfOver(
                    MainConfig.Elimination.healthScale * 2,
                    MainConfig.Elimination.getMin(),
                    player,
                    true
            );

            if(HealthUtils.get(player) <= 0)
                eliminate(player);
            return;
        }

        if (!MainConfig.Elimination.environmentStealsHearts)
            return;

        if(MainConfig.Elimination.alwaysDropHearts) {
            Util.drop(
                    getPlugin()
                            .getItem("heart_item")
                            .orElseThrow()
                            .getItemStack(),
                    player.getEyeLocation()
            );
        }

        HealthUtils.decreaseIfOver(MainConfig.Elimination.environmentHealthScale * 2, MainConfig.Elimination.getMin(), player, true);

        if(HealthUtils.get(player) <= 0)
            eliminate(player);
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

            if(MainConfig.Elimination.Bans.broadcastBan) {
                Bukkit.broadcastMessage(MainConfig.Elimination.Bans.broadcastMessage.replace("%player%", player.getDisplayName()));
            }

            if(MainConfig.Elimination.Bans.useBanTime) {
                System.out.println("Banning " + player.getName() + " for " + MainConfig.Elimination.Bans.banTime + " time");
                System.out.println(StringUtils.parseBanTime(MainConfig.Elimination.Bans.banTime));
                getPlugin().getEliminationHandler().eliminate(player, StringUtils.parseBanTime(MainConfig.Elimination.Bans.banTime));
            }
            else getPlugin().getEliminationHandler().eliminate(player);

            player.kickPlayer(MainConfig.Elimination.Bans.banMessage);
        }, 1);
    }

}
