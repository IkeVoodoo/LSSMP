package me.ikevoodoo.lssmp.configuration.parsers.items.custom;

import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.helix.api.config.builder.ConfigurationBuilder;
import me.ikevoodoo.helix.api.config.parsing.CompoundTypeParser;
import me.ikevoodoo.helix.api.items.display.parsers.ItemDisplayDataParser;
import me.ikevoodoo.lssmp.configuration.data.items.custom.ReviveBeaconConfiguration;
import me.ikevoodoo.lssmp.configuration.parsers.recipes.RecipeConfigurationParser;
import org.jetbrains.annotations.NotNull;

public class ReviveBeaconParser implements CompoundTypeParser<ReviveBeaconConfiguration> {
    @Override
    public @NotNull Class<ReviveBeaconConfiguration> complexType() {
        return ReviveBeaconConfiguration.class;
    }

    @Override
    public @NotNull ReviveBeaconConfiguration deserialize(@NotNull Configuration value) {
        return new ReviveBeaconConfiguration(
                value.getCompound("display"),
                value.getCompound("recipe"),
                value.getValue("id"),
//                value.getValue("maxRevives"),
                -1,
                value.getValue("heartCost"),
                value.getValue("enabled"),
                value.getValue("craftable")
        );
    }

    @Override
    public void serialize(@NotNull Configuration section, @NotNull ReviveBeaconConfiguration value) {
        section.value("id").value(value.getId());
//        section.value("maxRevives").value(value.getMaxRevives());
        section.value("heartCost").value(value.getHeartCost());
        section.value("enabled").value(value.isEnabled());
        section.value("craftable").value(value.isCraftable());
        section.compound("display").value(value.getDisplayData());
        section.compound("recipe").value(value.getRecipeConfiguration());
    }

    @Override
    public void setup(@NotNull ConfigurationBuilder template, @NotNull ReviveBeaconConfiguration value) {
        template.value("id", value.getId())
                .comment("The id of the item, you can use this in recipes.")
                .comment("Note: This ID should be unique.")
                .next();

        template.value("enabled", value.isEnabled())
                .comment("Can this item be used?.")
                .next();

        template.value("craftable", value.isCraftable())
                .comment("Can this item be crafted?")
                .next();

//        template.value("maxRevives", value.getMaxRevives())
//                .comment("How many players can this beacon revive?")
//                .comment("Note: Use -1 for infinite.")
//                .next();

        template.value("heartCost", value.getHeartCost())
                .comment("How many hearts does it cost to revive a player?")
                .comment("Note: Use 0 for none.")
                .next();

        template.compound("display", value.getDisplayData(), new ItemDisplayDataParser()).next();
        template.compound("recipe", value.getRecipeConfiguration(), new RecipeConfigurationParser()).next();
    }
}
