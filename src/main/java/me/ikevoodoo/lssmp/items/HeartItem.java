package me.ikevoodoo.lssmp.items;

import me.ikevoodoo.lssmp.config.ItemConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.items.ItemClickResult;
import me.ikevoodoo.smpcore.items.ItemClickState;
import me.ikevoodoo.smpcore.text.messaging.MessageBuilder;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import me.ikevoodoo.smpcore.utils.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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
        int removeAmount = 1;
        if (player.isSneaking()) {
            removeAmount = itemStack.getAmount();
        }

        while (removeAmount > 0) {
            HealthUtils.SetResult result = HealthUtils.increaseIfUnder(
                    MainConfig.Elimination.healthScale * 2 * removeAmount,
                    MainConfig.Elimination.getMax(),
                    player
            );
            if(!result.isOutOfBounds())
                break;
            removeAmount--;
        }

        if(removeAmount > 0) {
            player.sendMessage(ItemConfig.HeartItem.Messages.increment.replace("%s", "" + MainConfig.Elimination.healthScale * removeAmount));
            if (ItemConfig.HeartItem.claimingHeartHeals) HealthUtils.heal(player, MainConfig.Elimination.healthScale * 2 * removeAmount);
            itemStack.setAmount(itemStack.getAmount() - removeAmount);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.BLOCKS, 1, 1.5f);
            return new ItemClickResult(ItemClickState.IGNORE, true);
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, .7f);
        player.sendMessage(ItemConfig.HeartItem.Messages.maxHearts);
        return new ItemClickResult(
                ItemClickState.FAIL,
                true
        );
    }
}
