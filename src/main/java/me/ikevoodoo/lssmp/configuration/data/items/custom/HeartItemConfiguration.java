package me.ikevoodoo.lssmp.configuration.data.items.custom;

import me.ikevoodoo.lssmp.configuration.data.recipes.RecipeConfiguration;
import me.ikevoodoo.lssmp.configuration.data.items.custom.messages.HeartItemMessages;
import me.ikevoodoo.helix.api.items.display.ItemDisplayData;

public class HeartItemConfiguration extends ItemConfiguration {

    private final HeartItemMessages messages;
    private final double heartMultiplier;
    private final boolean healing;
    private final double maxClaim;

    public HeartItemConfiguration(ItemDisplayData displayData,
                                  RecipeConfiguration recipeConfiguration,
                                  double heartMultiplier,
                                  String id,
                                  HeartItemMessages messages,
                                  boolean healing,
                                  boolean craftable,
                                  double maxClaim,
                                  boolean enabled) {
        super(displayData, recipeConfiguration, craftable, id, enabled);
        this.heartMultiplier = heartMultiplier;
        this.messages = messages;
        this.healing = healing;
        this.maxClaim = maxClaim;
    }

    public double getHeartMultiplier() {
        return this.heartMultiplier;
    }

    public boolean isHealing() {
        return this.healing;
    }

    public HeartItemMessages getMessages() {
        return this.messages;
    }

    public double getMaxClaim() {
        return this.maxClaim;
    }
}
