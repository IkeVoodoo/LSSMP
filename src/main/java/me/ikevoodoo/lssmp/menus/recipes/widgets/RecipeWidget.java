package me.ikevoodoo.lssmp.menus.recipes.widgets;

import me.ikevoodoo.smpcore.menus.v2.Page;
import me.ikevoodoo.smpcore.menus.v2.widgets.Widget;
import me.ikevoodoo.smpcore.utils.RecipeUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.RecipeChoice;

public final class RecipeWidget extends Widget {

    private final RecipeChoice[] recipeChoices;
    private final int x;
    private final int y;

    public RecipeWidget(Page page, RecipeChoice[] recipeChoices, int x, int y) {
        super(page);
        this.recipeChoices = recipeChoices;
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Inventory inventory, Player player) {
        var size = this.recipeChoices.length / 3;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int posX = this.x + x;
                int posY = this.y + y;

                int inventoryPos = posX + posY * 9;
                int choiceIndex = posX + posY * size;

                inventory.setItem(inventoryPos, RecipeUtils.getStack(this.recipeChoices[choiceIndex]));
            }
        }
    }

}
