package me.ikevoodoo.lssmp.listeners;

import me.ikevoodoo.lssmp.config.ConfigFile;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.events.PlayerPreDeathEvent;
import me.ikevoodoo.smpcore.listeners.SMPListener;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import me.ikevoodoo.smpcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        if(!ConfigFile.Elimination.isWorldAllowed(world))
            return;

        if((!event.hasKiller() || !(event.getKiller() instanceof Player)) && !ConfigFile.Elimination.environmentStealsHearts)
            return;

        if(event.hasKiller() && event.getKiller() instanceof Player killer)
            HealthUtils.increaseIfUnder(ConfigFile.Elimination.healthScale, ConfigFile.Elimination.getMax(), killer);

        if(!HealthUtils.decreaseIfOver(ConfigFile.Elimination.environmentHealthScale * 2, 0, player))
            eliminate(player);

        if(HealthUtils.get(player) <= 0)
            eliminate(player);
    }

    private void eliminate(Player player) {
        player.kickPlayer("§cYou have been eliminated!");
        if(ConfigFile.Elimination.Bans.broadcastBan) {
            Bukkit.broadcastMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            ConfigFile.Elimination.Bans.banTime.replace("%player%", player.getDisplayName()))
            );
        }

        if(ConfigFile.Elimination.Bans.useBanTime) {
            getPlugin().getEliminationHandler().eliminate(player, StringUtils.parseBanTime(ConfigFile.Elimination.Bans.banTime));
            return;
        }

        getPlugin().getEliminationHandler().eliminate(player);
    }

}
