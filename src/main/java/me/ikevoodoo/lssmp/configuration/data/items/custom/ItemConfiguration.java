package me.ikevoodoo.lssmp.configuration.data.items.custom;

import me.ikevoodoo.helix.api.items.display.ItemDisplayData;
import me.ikevoodoo.lssmp.configuration.data.recipes.RecipeConfiguration;

public abstract class ItemConfiguration {

    private final ItemDisplayData displayData;
    private final RecipeConfiguration recipeConfiguration;
    private final boolean craftable;
    private final String id;
    private final boolean enabled;

    protected ItemConfiguration(ItemDisplayData displayData, RecipeConfiguration recipeConfiguration, boolean craftable, String id, boolean enabled) {
        this.displayData = displayData;
        this.recipeConfiguration = recipeConfiguration;
        this.craftable = craftable;
        this.id = id;
        this.enabled = enabled;
    }

    public ItemDisplayData getDisplayData() {
        return this.displayData;
    }

    public RecipeConfiguration getRecipeConfiguration() {
        return this.recipeConfiguration;
    }

    public String getId() {
        return this.id;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isCraftable() {
        return this.craftable;
    }


    @Override
    public String toString() {
        return "ItemConfiguration[" +
                "craftable=" + isCraftable() +
                ", displayData=" + getDisplayData() +
                ", recipeConfiguration=" + getRecipeConfiguration() +
                ", id='" + getId() + '\'' +
                ", enabled=" + isEnabled() +
                ']';
    }
}
