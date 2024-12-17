package me.ikevoodoo.lssmp.configuration.parsers.items.custom;

import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.helix.api.config.builder.ConfigurationBuilder;
import me.ikevoodoo.helix.api.config.parsing.CompoundTypeParser;
import me.ikevoodoo.helix.api.items.display.parsers.ItemDisplayDataParser;
import me.ikevoodoo.lssmp.configuration.data.items.custom.HeartItemConfiguration;
import me.ikevoodoo.lssmp.configuration.parsers.items.custom.messages.HeartItemMessagesParser;
import me.ikevoodoo.lssmp.configuration.parsers.recipes.RecipeConfigurationParser;
import org.jetbrains.annotations.NotNull;

public class HeartItemParser implements CompoundTypeParser<HeartItemConfiguration> {
    @Override
    public @NotNull Class<HeartItemConfiguration> complexType() {
        return HeartItemConfiguration.class;
    }

    @Override
    public @NotNull HeartItemConfiguration deserialize(@NotNull Configuration configuration) {
        return new HeartItemConfiguration(
                configuration.getCompound("display"),
                configuration.getCompound("recipe"),
                configuration.getValue("heartMultiplier"),
                configuration.getValue("id"),
                configuration.getCompound("messages"),
                configuration.getValue("healing"),
                configuration.getValue("craftable"),
                configuration.getValue("maxClaim"),
                configuration.getValue("enabled")
        );
    }

    @Override
    public void serialize(@NotNull Configuration configuration, @NotNull HeartItemConfiguration item) {
        configuration.value("id").value(item.getId());
        configuration.value("heartMultiplier").value(item.getHeartMultiplier());
        configuration.value("craftable").value(item.isCraftable());
        configuration.value("maxClaim").value(item.getMaxClaim());
        configuration.value("enabled").value(item.isEnabled());
        configuration.compound("display").value(item.getDisplayData());
        configuration.compound("recipe").value(item.getRecipeConfiguration());
        configuration.compound("messages").value(item.getMessages());
    }

    @Override
    public void setup(@NotNull ConfigurationBuilder template, @NotNull HeartItemConfiguration item) {
        template.value("id", item.getId())
                .comment("The id of the item, you can use this in recipes.")
                .comment("Note: This ID should be unique.")
                .next();

        template.value("enabled", item.isEnabled())
                .comment("Can this item be used?")
                .next();

        template.value("heartMultiplier", item.getHeartMultiplier())
                .comment("How many hearts does this heart contain?")
                .next();

        template.value("healing", item.isHealing())
                .comment("Does this heart item also grant health?")
                .comment("Note: The healing amount is the same as the heart multiplier")
                .next();

        template.value("craftable", item.isCraftable())
                .comment("Can this heart item be crafted?")
                .next();

        template.value("maxClaim", item.getMaxClaim())
                .comment("What is the maximum amount of hearts a player can have before they can't use this heart anymore?")
                .comment("Note: Use -1 to disable this.")
                .comment("Note: Even if this is uncapped (using -1) or higher than the overall max hearts, players are still unable to go above the global maximum.")
                .next();

        template.compound("display", item.getDisplayData(), new ItemDisplayDataParser()).next();
        template.compound("messages", item.getMessages(), new HeartItemMessagesParser()).next();
        template.compound("recipe", item.getRecipeConfiguration(), new RecipeConfigurationParser()).next();
    }
}
