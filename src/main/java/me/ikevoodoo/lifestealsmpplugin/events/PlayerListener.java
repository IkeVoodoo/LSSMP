package me.ikevoodoo.lifestealsmpplugin.events;

import me.ikevoodoo.lifestealsmpplugin.Configuration;
import me.ikevoodoo.lifestealsmpplugin.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

import static me.ikevoodoo.lifestealsmpplugin.Utils.*;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKill(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if(killer != null) {
            modifyHealth(killer, 2);
            if(shouldEliminate(killed)) {
                eliminate(killed, killer);
                //event.setCancelled(true);
                return;
            }
            modifyHealth(killed, -2);
            return;
        } else {
            if(shouldEliminate(killed)) {
                eliminate(killed, null);
                //event.setCancelled(true);
                return;
            }
            modifyHealth(killed, -2);
        }

        String killerUUID = Configuration.getKiller(killed.getUniqueId());
        if(killerUUID != null) {
            Player player = Bukkit.getPlayer(UUID.fromString(killerUUID));
            if(player != null) killed.setSpectatorTarget(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player respawned = event.getPlayer();
        String killerUUID = Configuration.getKiller(respawned.getUniqueId());
        if(killerUUID != null) {
            Player player = Bukkit.getPlayer(UUID.fromString(killerUUID));
            if(player != null) respawned.setSpectatorTarget(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        if(Configuration.isBanned(event.getUniqueId()))
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    Utils.getFromText(
                            Configuration.getBanMessage(event.getUniqueId())
                    )
            );
        else if(Configuration.isEliminated(event.getUniqueId())) {
            String killerUUID = Configuration.getKiller(event.getUniqueId());
            if(killerUUID == null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        Utils.getFromText(
                                Configuration.getKillerNotOnline()
                        )
                );
                return;
            }
            Player player = Bukkit.getPlayer(UUID.fromString(killerUUID));
            if(player == null) {
                UUID id = UUID.fromString(killerUUID);
                if(!Configuration.getKilled(id).equals(event.getUniqueId().toString())) {
                    Configuration.reviveOnlyDead(id);
                    OfflinePlayer off = Bukkit.getOfflinePlayer(id);
                    Configuration.banID(id, Configuration.getBanMessage().replace("%player%", off.getName() == null ? "unknown" : off.getName()));
                }
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        Utils.getFromText(
                                Configuration.getKillerNotOnline()
                        )
                );
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisconnect(PlayerQuitEvent event) {
        String killedUUID = Configuration.getKilled(event.getPlayer().getUniqueId());
        if(killedUUID != null) {
            Player player = Bukkit.getPlayer(UUID.fromString(killedUUID));
            if(player != null) {
                player.kickPlayer(
                        Utils.getFromText(
                                Configuration.getKillerDisconnected()
                        )
                );
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGamemodeSwitch(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (Configuration.isEliminated(player)) {
            event.setCancelled(true);
        }
    }

}
