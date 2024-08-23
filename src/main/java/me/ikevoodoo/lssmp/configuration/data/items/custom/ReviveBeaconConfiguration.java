package me.ikevoodoo.lssmp.configuration.data.items.custom;

import me.ikevoodoo.helix.api.items.display.ItemDisplayData;
import me.ikevoodoo.lssmp.configuration.data.recipes.RecipeConfiguration;

public class ReviveBeaconConfiguration extends ItemConfiguration {

    private final int maxRevives;
    private final int heartCost;

    public ReviveBeaconConfiguration(ItemDisplayData displayData, RecipeConfiguration recipeConfiguration, String id, int maxRevives, int heartCost, boolean enabled, boolean craftable) {
        super(displayData, recipeConfiguration, craftable, id, enabled);
        this.maxRevives = maxRevives;
        this.heartCost = heartCost;
    }

    public int getMaxRevives() {
        return this.maxRevives;
    }

    public int getHeartCost() {
        return this.heartCost;
    }
}
