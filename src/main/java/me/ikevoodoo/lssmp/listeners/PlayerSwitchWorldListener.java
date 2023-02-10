package me.ikevoodoo.lssmp.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.listeners.SMPListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerSwitchWorldListener extends SMPListener {
    public PlayerSwitchWorldListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(PlayerChangedWorldEvent event) {
        getPlugin().getHealthHelper().updateHealth(event.getPlayer());
    }
}
