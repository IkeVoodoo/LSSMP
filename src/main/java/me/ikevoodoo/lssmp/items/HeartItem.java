package me.ikevoodoo.lssmp.items;

import me.ikevoodoo.lssmp.config.ItemConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.items.ItemClickResult;
import me.ikevoodoo.smpcore.items.ItemClickState;
import me.ikevoodoo.smpcore.messaging.MessageBuilder;
import me.ikevoodoo.smpcore.recipes.RecipeData;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import me.ikevoodoo.smpcore.utils.Pair;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class HeartItem extends CustomItem {
    private static final MessageBuilder NAME_BUILDER = new MessageBuilder().add("Heart Item", ChatColor.RED);

    public HeartItem(SMPPlugin plugin) {
        super(plugin, "heart_item", NAME_BUILDER.build());
        addKey("heart")
                .setDecreaseOnUse(true)
                .bindConfig("items.heart")
                .bindConfigOptions("heartRecipe.yml", "options")
                .reload();
    }

    @Override
    public ItemStack createItem(Player player) {
        return new ItemStack(getRecipeOptions().mat());
    }

    @Override
    public Pair<NamespacedKey, Recipe> createRecipe() {
        unlockOnObtain(getRecipeData().materials());
        return new Pair<>(makeKey("heart_recipe"), getRecipeData().recipe());
    }

    @Override
    public RecipeData createRecipeData() {
        return getPlugin().getRecipeLoader().getRecipe(
                getPlugin().getConfigHandler().getYmlConfig("heartRecipe.yml"),
                "recipe",
                getItemStack(),
                makeKey("heart_recipe"),
                getRecipeOptions()
        );
    }

    @Override
    public ItemClickResult onClick(Player player, ItemStack itemStack, Action action) {
        int removeAmount = 1;
        if (player.isSneaking()) {
            removeAmount = itemStack.getAmount();
        }

        if(HealthUtils.increaseIfUnder(MainConfig.Elimination.healthScale * 2 * removeAmount, MainConfig.Elimination.getMax(), player)) {
            player.sendMessage(ItemConfig.HeartItem.Messages.increment.replace("%s", "" + MainConfig.Elimination.healthScale * removeAmount));
            HealthUtils.heal(player, MainConfig.Elimination.healthScale * 2 * removeAmount);
            itemStack.setAmount(itemStack.getAmount() - removeAmount);
            return new ItemClickResult(ItemClickState.IGNORE, true);
        }

        player.sendMessage(ItemConfig.HeartItem.Messages.maxHearts);
        return new ItemClickResult(
                ItemClickState.FAIL,
                true
        );
    }
}
