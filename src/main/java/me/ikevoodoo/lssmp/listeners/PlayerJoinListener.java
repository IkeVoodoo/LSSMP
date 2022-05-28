package me.ikevoodoo.lssmp.listeners;

import me.ikevoodoo.lssmp.config.ConfigFile;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.listeners.SMPListener;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener extends SMPListener {
    public PlayerJoinListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        if(!event.getPlayer().hasPlayedBefore()) {
            HealthUtils.set(ConfigFile.Elimination.defaultHearts * 2, event.getPlayer());
        }
    }
}
