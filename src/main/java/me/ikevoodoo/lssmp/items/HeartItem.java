package me.ikevoodoo.lssmp.items;

import me.ikevoodoo.lssmp.config.ItemConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.items.ItemClickResult;
import me.ikevoodoo.smpcore.items.ItemClickState;
import me.ikevoodoo.smpcore.text.messaging.MessageBuilder;
import me.ikevoodoo.smpcore.utils.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class HeartItem extends CustomItem {
    public HeartItem(SMPPlugin plugin) {
        super(plugin, "heart_item", MessageBuilder.messageOf("§c§lHeart Item"));
        addKey("heart")
                .setDecreaseOnUse(true)
                .bindConfig("items.heart")
                .bindConfigOptions("heartRecipe.yml", "options")
                .setRecipeFile("heartRecipe.yml")
                .setAllowCombustion(false)
                .setAllowCactusDamage(false)
                .reload();
    }

    @Override
    public ItemStack createItem(Player player) {
        return new ItemStack(getRecipeOptions().mat());
    }

    @Override
    public Pair<NamespacedKey, Recipe> createRecipe() {
        unlockOnObtain(getRecipeData().materials());
        return new Pair<>(makeKey("heart_item_recipe"), getRecipeData().recipe());
    }

    @Override
    public ItemClickResult onClick(Player player, ItemStack itemStack, Action action) {
        var heartsToMax = getHeartsToMax(player);
        if (heartsToMax <= 0) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, .7f);
            player.sendMessage(ItemConfig.HeartItem.Messages.maxHearts);
            return new ItemClickResult(
                    ItemClickState.FAIL,
                    true
            );
        }


        var maxItems = this.getMaxItems(heartsToMax, itemStack.getAmount(), player.isSneaking());
        var healthToAdd = maxItems * MainConfig.Elimination.getHeartScale();

        var message = ItemConfig.HeartItem.Messages.increment.replace("%s", String.valueOf(maxItems));
        player.sendMessage(message);

        var newHealth = getPlugin().getHealthHelper().increaseMaxHealth(player, healthToAdd);
        if(ItemConfig.HeartItem.claimingHeartHeals) {
            var health = player.getHealth();
            var toSet = health + healthToAdd;

            player.setHealth(Math.min(toSet, newHealth));
        }

        itemStack.setAmount(itemStack.getAmount() - maxItems);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1.5f);
        return new ItemClickResult(
                ItemClickState.IGNORE,
                true
        );
    }

    private double getHeartsToMax(Player player) {
        var max = MainConfig.Elimination.getMax();
        var current = getPlugin().getHealthHelper().getMaxHealth(player);

        var diff = max - current;
        if (diff <= 0) return 0;

        return diff;
    }

    private int getMaxItems(double healthToMax, int stackSize, boolean isSneaking) {
        if (!isSneaking) {
            return healthToMax >= 1 ? 1 : 0;
        }

        var maxItems = (int) Math.floor(healthToMax / MainConfig.Elimination.getHeartScale());
        return Math.min(maxItems, stackSize);
    }
}
