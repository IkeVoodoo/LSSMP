package me.ikevoodoo.lifestealsmpplugin.events;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import me.ikevoodoo.lifestealsmpplugin.Configuration;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import static me.ikevoodoo.lifestealsmpplugin.Utils.*;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKill(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if(killer != null) {
            if(shouldEliminate(killed)) {
                eliminate(killed, killer);
                return;
            }
            modifyHealth(killer, 2);
            modifyHealth(killed, -2);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGamemodeSwitch(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (Configuration.isEliminated(player)) {
            event.setCancelled(true);
            player.setGameMode(GameMode.SPECTATOR); // Just in case it does not cancel
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpectatingStopped(PlayerStopSpectatingEntityEvent event) {
        Player player = event.getPlayer();
        if(Configuration.isEliminated(player)) {
            event.setCancelled(true);
            player.setSpectatorTarget(event.getSpectatorTarget()); // Just in case it does not cancel, like gamemode ;)
        }
    }

}
