package me.ikevoodoo.lssmp.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.events.MenuEvent;
import me.ikevoodoo.smpcore.listeners.SMPListener;
import me.ikevoodoo.smpcore.menus.Menu;
import org.bukkit.event.EventHandler;

public class MenuEventListener extends SMPListener {

    public MenuEventListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(MenuEvent event) {
        Menu menu = event.getMenu();

        if (!getPlugin().getMenuHandler().has(menu.id())) return;
        if (menu.id().getKey().startsWith("lssmp_recipe_editor_")) return;

        event.setCancelled(true);
    }
}
