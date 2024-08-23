package me.ikevoodoo.lssmp.screens.components;

import me.ikevoodoo.helix.api.screens.components.HelixComponentContext;
import me.ikevoodoo.helix.api.screens.components.HelixComponentEvent;
import me.ikevoodoo.helix.api.screens.components.HelixComponentHandler;
import me.ikevoodoo.helix.api.screens.components.HelixPageComponent;
import org.bukkit.inventory.ItemStack;

public class SingleItemHandler implements HelixComponentHandler {

    private final ItemStack stack;

    public SingleItemHandler(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void render(HelixComponentContext context, HelixPageComponent component) {
        context.setItem(component.position(0, 0), this.stack);
    }

    @Override
    public void handleEvent(HelixComponentEvent event, HelixComponentContext context, HelixPageComponent component) {
        event.cancel();
    }

    @Override
    public void close(HelixComponentContext context, HelixPageComponent component) {

    }
}
