package me.ikevoodoo.lssmp.menus.recipes;

import me.ikevoodoo.lssmp.menus.recipes.widgets.RecipeWidget;
import me.ikevoodoo.lssmp.menus.widgets.ScrollButton;
import me.ikevoodoo.smpcore.menus.v2.Page;
import me.ikevoodoo.smpcore.menus.v2.PagedMenu;
import me.ikevoodoo.smpcore.utils.RecipeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

public final class RecipePage extends Page {

    private final RecipeChoice[] recipeChoices;
    private final ItemStack result;

    public RecipePage(int index, PagedMenu menu, Recipe recipe) {
        super(index, 9, 6, menu);

        this.recipeChoices = RecipeUtils.getChoices(recipe);
        this.result = recipe.getResult();

        this.setBackground(Material.GRAY_STAINED_GLASS_PANE);

        this.addWidget(new ScrollButton(this, 1, 6, false));
        this.addWidget(new ScrollButton(this, 9, 6, true));

        this.addWidget(new RecipeWidget(this, this.recipeChoices, 3, 2));

//        this.addWidgetWhen(new RecipeSettingsButton(9, 1), (inventory, player) -> {
//            return player.hasPermission("lssmp.recipe.settings");
//        });
    }

    @Override
    protected void draw(Inventory inventory, Player player) {
        
    }

}
