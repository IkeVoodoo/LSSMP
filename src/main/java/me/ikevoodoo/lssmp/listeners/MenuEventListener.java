package me.ikevoodoo.lssmp.listeners;

import me.ikevoodoo.lssmp.LSSMP;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.events.MenuEvent;
import me.ikevoodoo.smpcore.listeners.SMPListener;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

public class MenuEventListener extends SMPListener {

    public MenuEventListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(MenuEvent event) {
        if (!event.getMenu().is(LSSMP.LSSMP_MENU)) return;

        if (event.getSlot() == 9 * 5 - 1 && event.getItem().getType() == Material.LIME_STAINED_GLASS_PANE)
            event.getMenu().next(event.getPlayer());
        else if (event.getSlot() == 9 * 4 && event.getItem().getType() == Material.LIME_STAINED_GLASS_PANE)
            event.getMenu().previous(event.getPlayer());

        event.setCancelled(true);
    }
}
