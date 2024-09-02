package me.ikevoodoo.lssmp.configuration.data.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Arrays;

public record RecipeConfiguration(boolean shaped, RecipeChoice[] choices) {

    public ItemStack[] getShapedStacks() {
        var shape = this.generateShape();
        var items = new ItemStack[9];

        var line = -1;

        for (int i = 0; i < this.choices.length; i++) {
            var choice = this.choices[i];
            if (choice == null) continue;

            if (i % 3 == 0) {
                line++;
            }

            var shapeStr = shape[line];
            var shapeCh = (char) ('0' + i);
            if (shapeStr.indexOf(shapeCh) == -1) continue;

            if (choice instanceof RecipeChoice.MaterialChoice mat) {
                items[i] = new ItemStack(mat.getItemStack());
            }

            if (choice instanceof RecipeChoice.ExactChoice exact) {
                items[i] = new ItemStack(exact.getItemStack());
            }
        }

        return items;
    }

    public Recipe createRecipe(NamespacedKey key, ItemStack result) {
        if (this.shaped) {
            var recipe = new ShapedRecipe(key, result);
            recipe.shape(this.generateShape());

            var shape = recipe.getShape();

            var line = 0;

            for (int i = 0; i < this.choices.length; i++) {
                var choice = this.choices[i];
                if (choice == null) continue;

                if (i != 0 && i % 3 == 0) {
                    line++;
                }

                var shapeStr = shape[line];
                var shapeCh = (char) ('0' + i);
                if (shapeStr.indexOf(shapeCh) == -1) continue;

                recipe.setIngredient(shapeCh, this.choices[i]);
            }

            return recipe;
        }

        var recipe = new ShapelessRecipe(key, result);

        for (var choice : this.choices) {
            if (choice == null) continue;

            recipe.addIngredient(choice);
        }

        return recipe;
    }

    public String[] generateShape() {
        var out = new StringBuilder[] {
                new StringBuilder("012"),
                new StringBuilder("345"),
                new StringBuilder("678")
        };

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                var index = y * 3 + x;

                var choice = this.choices[index];
                if (choice == null) {
                    out[x].setCharAt(y, ' ');
                    continue;
                }

                if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                    var materials = new ArrayList<Material>();

                    for (var mat : materialChoice.getChoices()) {
                        if (mat == null || mat.isAir()) continue;

                        materials.add(mat);
                    }

                    if (materials.isEmpty()) {
                        out[x].setCharAt(y, ' ');
                        continue;
                    }

                    this.choices[index] = new RecipeChoice.MaterialChoice(materials);
                }

                if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
                    var items = new ArrayList<ItemStack>();

                    for (var item : exactChoice.getChoices()) {
                        if (item == null || item.getType().isAir()) continue;

                        items.add(item);
                    }

                    if (items.isEmpty()) {
                        out[x].setCharAt(y, ' ');
                        continue;
                    }

                    this.choices[index] = new RecipeChoice.ExactChoice(items);
                }
            }
        }

        return new String[] {
                out[0].toString(),
                out[1].toString(),
                out[2].toString()
        };
    }

    @Override
    public String toString() {
        return "RecipeConfiguration[" +
                "choices=" + Arrays.toString(choices) +
                ", shaped=" + shaped +
                ']';
    }
}
