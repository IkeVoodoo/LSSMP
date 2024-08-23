package me.ikevoodoo.lssmp.configuration.parsers.recipes;

import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.helix.api.config.builder.ConfigurationBuilder;
import me.ikevoodoo.helix.api.config.parsing.CompoundTypeParser;
import me.ikevoodoo.helix.api.items.HelixItem;
import me.ikevoodoo.helix.api.items.display.ItemDisplayData;
import me.ikevoodoo.lssmp.configuration.parsers.items.ItemStackParser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public class RecipeChoiceParser implements CompoundTypeParser<RecipeChoice> {

    private final Function<HelixItem, ItemDisplayData> displayDataFunction;

    public RecipeChoiceParser(Function<HelixItem, ItemDisplayData> displayDataFunction) {
        this.displayDataFunction = displayDataFunction;
    }

    @Override
    public @NotNull Class<RecipeChoice> complexType() {
        return RecipeChoice.class;
    }

    @Override
    public @NotNull RecipeChoice deserialize(@NotNull Configuration configuration) {
        Material[] choices = configuration.getValueArray("materials");
        if (choices != null) {
            return new RecipeChoice.MaterialChoice(choices);
        }

        ItemStack[] items = configuration.getValueArray("items");
        if (items != null) {
            return new RecipeChoice.ExactChoice(items);
        }

        throw new IllegalStateException("Unable to deserialize RecipeChoice");
    }

    @Override
    public void serialize(@NotNull Configuration configuration, @NotNull RecipeChoice recipeChoice) {
        if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
            configuration.valueArray("materials").values(materialChoice.getChoices().toArray(new Material[0]));
            return;
        }

        if (recipeChoice instanceof RecipeChoice.ExactChoice exactChoice) {
            configuration.valueArray("items").values(exactChoice.getChoices().toArray(new ItemStack[0]));
        }
    }

    @Override
    public void setup(@NotNull ConfigurationBuilder configurationBuilder, @NotNull RecipeChoice recipeChoice) {
        if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
            configurationBuilder.valueArray("materials", materialChoice.getChoices().toArray(new Material[0])).next();
            return;
        }

        if (recipeChoice instanceof RecipeChoice.ExactChoice exactChoice) {
            configurationBuilder.valueArray("items", exactChoice.getChoices().toArray(new ItemStack[0]), new ItemStackParser(this.displayDataFunction)).next();
        }
    }

    @Override
    public void setup(@NotNull ConfigurationBuilder configurationBuilder, @NotNull Map<?, ?> map) {
        if (map.containsKey("materials")) {
            configurationBuilder.valueArray("materials", new Material[0]).next();
            return;
        }

        if (map.containsKey("items")) {
            configurationBuilder.valueArray("items", new ItemStack[0]).next();
        }
    }
}
