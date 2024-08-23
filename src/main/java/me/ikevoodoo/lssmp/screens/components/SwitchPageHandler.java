package me.ikevoodoo.lssmp.screens.components;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.namespaced.UniqueIdentifier;
import me.ikevoodoo.helix.api.screens.components.HelixComponentContext;
import me.ikevoodoo.helix.api.screens.components.HelixComponentEvent;
import me.ikevoodoo.helix.api.screens.components.HelixComponentHandler;
import me.ikevoodoo.helix.api.screens.components.HelixPageComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SwitchPageHandler implements HelixComponentHandler {

    private final UniqueIdentifier screen;
    private final String page;

    public SwitchPageHandler(UniqueIdentifier screen, String page) {
        this.screen = screen;
        this.page = page;
    }

    @Override
    public void render(HelixComponentContext context, HelixPageComponent component) {
        context.setItem(component.position(0), new ItemStack(Material.GREEN_STAINED_GLASS_PANE));
    }

    @Override
    public void handleEvent(HelixComponentEvent event, HelixComponentContext context, HelixPageComponent component) {
        event.cancel();

        Helix.screens().open(event.player(), this.screen, this.page);
    }

    @Override
    public void close(HelixComponentContext context, HelixPageComponent component) {

    }
}
