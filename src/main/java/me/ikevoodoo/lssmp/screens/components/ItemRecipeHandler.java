package me.ikevoodoo.lssmp.screens.components;

import me.ikevoodoo.helix.api.screens.components.HelixComponentContext;
import me.ikevoodoo.helix.api.screens.components.HelixComponentEvent;
import me.ikevoodoo.helix.api.screens.components.HelixComponentHandler;
import me.ikevoodoo.helix.api.screens.components.HelixPageComponent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ItemRecipeHandler implements HelixComponentHandler {

    private final ItemStack[] recipe;

    public ItemRecipeHandler(ItemStack[] recipe) {
        this.recipe = recipe;
    }

    @Override
    public void render(HelixComponentContext context, HelixPageComponent component) {
        System.out.println(Arrays.toString(this.recipe));
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                var slot = y * 3 + x;
                if (slot >= this.recipe.length) {
                    context.setItem(component.position(x, y), null);
                    continue;
                }

                context.setItem(component.position(x, y), this.recipe[slot]);
            }
        }
    }

    @Override
    public void handleEvent(HelixComponentEvent event, HelixComponentContext context, HelixPageComponent component) {
        event.cancel();
    }

    @Override
    public void close(HelixComponentContext context, HelixPageComponent component) {

    }

}
