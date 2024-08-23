package me.ikevoodoo.lssmp.screens;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.items.HelixItem;
import me.ikevoodoo.helix.api.namespaced.UniqueIdentifier;
import me.ikevoodoo.helix.api.screens.HelixScreen;
import me.ikevoodoo.helix.api.screens.ScreenDimensions;
import me.ikevoodoo.helix.api.screens.SlotPosition;
import me.ikevoodoo.helix.api.screens.setup.HelixScreenSetup;
import me.ikevoodoo.lssmp.LifestealInit;
import me.ikevoodoo.lssmp.configuration.data.recipes.RecipeConfiguration;
import me.ikevoodoo.lssmp.screens.components.ItemRecipeHandler;
import me.ikevoodoo.lssmp.screens.components.SingleItemHandler;
import me.ikevoodoo.lssmp.screens.components.SwitchPageHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

import static me.ikevoodoo.lssmp.Constants.RECIPE_SCREEN_ID;

public class RecipeScreen implements HelixScreen {

    private final Map<String, RecipeConfiguration> recipes;

    public RecipeScreen(Map<String, RecipeConfiguration> recipes) {
        this.recipes = recipes;
    }

    @Override
    public void setup(HelixScreenSetup setup) {
        System.out.println(this.recipes);

        var ids = new HashMap<UniqueIdentifier, HelixItem>();

        for (var itemKey : Helix.items().getKeys(JavaPlugin.getPlugin(LifestealInit.class))) {
            var recipe = this.recipes.get(itemKey.key());
            if (recipe == null || !recipe.shaped()) continue;

            ids.put(itemKey, Helix.items().getItem(itemKey));
        }

        String last = null;

        for (var iterator = ids.entrySet().stream().toList().listIterator(); iterator.hasNext(); ) {
            var entry = iterator.next();
            var identifier = entry.getKey();
            var id = identifier.key();
            var item = entry.getValue();

            String next = null;
            if (iterator.hasNext()) {
                next = iterator.next().getKey().key();
                iterator.previous();
            }

            var recipe = this.recipes.get(id);

            var dim = ScreenDimensions.chest(5);
            var page = setup.createPage(id, item.defaultDisplayData().textDisplayData().displayName(), dim);
            var recipeComp = page.addComponent("recipe", new ItemRecipeHandler(recipe.getShapedStacks()));
            assert recipeComp != null;

            recipeComp.position(SlotPosition.fromXY(2, 1, dim));
            recipeComp.dimensions(new ScreenDimensions(null, 3, 3, 9));

            if (last != null) {
                var lastComp = page.addComponent("last", new SwitchPageHandler(RECIPE_SCREEN_ID, last));
                assert lastComp != null;

                lastComp.position(SlotPosition.fromXY(0, 4, dim));
                lastComp.dimensions(new ScreenDimensions(null, 1, 1, 1));
            }

            if (next != null) {
                var nextComp = page.addComponent("next", new SwitchPageHandler(RECIPE_SCREEN_ID, next));
                assert nextComp != null;

                nextComp.position(SlotPosition.fromXY(8, 4, dim));
                nextComp.dimensions(new ScreenDimensions(null, 1, 1, 1));
            }

            var resultComp = page.addComponent("result", new SingleItemHandler(Helix.items().createItem(identifier, null)));
            assert resultComp != null;

            resultComp.position(SlotPosition.fromXY(6, 2, dim));
            resultComp.dimensions(new ScreenDimensions(null, 1, 1, 1));

            page.setBackground(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

            last = id;
        }
    }
}
