package me.ikevoodoo.lssmp.items;

import me.ikevoodoo.lssmp.config.ConfigFile;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.items.ItemClickResult;
import me.ikevoodoo.smpcore.items.ItemClickState;
import me.ikevoodoo.smpcore.recipes.RecipeData;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import me.ikevoodoo.smpcore.utils.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class HeartItem extends CustomItem {
    public HeartItem(SMPPlugin plugin) {
        super(plugin, "heart_item");
        addKey("heart")
                .setDecreaseOnUse(true)
                .bindConfig("items.heart")
                .bindConfigOptions(getPlugin().getConfigHandler().getYmlConfig("heartRecipe.yml").getConfigurationSection("options"))
                .reload();
    }

    @Override
    public ItemStack createItem(Player player) {
        return new ItemStack(getRecipeOptions().mat());
    }

    @Override
    public Pair<NamespacedKey, Recipe> createRecipe() {
        NamespacedKey key = makeKey("heart_recipe");
        RecipeData data = getPlugin().getRecipeLoader().getRecipe(
                getPlugin().getConfigHandler().getYmlConfig("heartRecipe.yml"),
                "recipe",
                getItemStack(),
                key,
                getRecipeOptions()
        );
        unlockOnObtain(data.materials());
        return new Pair<>(key, data.recipe());
    }

    @Override
    public ItemClickResult onClick(Player player, ItemStack itemStack, Action action) {
        if(HealthUtils.increaseIfUnder(ConfigFile.Elimination.healthScale * 2, ConfigFile.Elimination.getMax(), player)) {
            player.sendMessage("§a+" + ConfigFile.Elimination.healthScale + " §4❤");
            player.setHealth(player.getHealth() + ConfigFile.Elimination.healthScale * 2);
            return new ItemClickResult(ItemClickState.SUCCESS, true);
        }

        player.sendMessage("§cYou have reached the maximum amount of hearts!");
        return new ItemClickResult(
                ItemClickState.FAIL,
                true
        );
    }
}
