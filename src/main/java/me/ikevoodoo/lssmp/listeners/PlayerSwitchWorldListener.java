package me.ikevoodoo.lssmp.listeners;

import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.listeners.SMPListener;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerSwitchWorldListener extends SMPListener {
    public PlayerSwitchWorldListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(PlayerChangedWorldEvent event) {
        if (!MainConfig.Elimination.isWorldAllowed(event.getPlayer().getWorld())) {
            HealthUtils.reset(event.getPlayer());
            return;
        }

        HealthUtils.apply(event.getPlayer());
    }
}
