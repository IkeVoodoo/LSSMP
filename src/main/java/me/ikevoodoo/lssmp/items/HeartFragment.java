package me.ikevoodoo.lssmp.items;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.text.messaging.MessageBuilder;
import me.ikevoodoo.smpcore.utils.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class HeartFragment extends CustomItem {
    public HeartFragment(SMPPlugin plugin) {
        super(plugin, "heart_fragment_item", MessageBuilder.messageOf("§c§lHeart Fragment"));
        addKey("heart_fragment")
                .bindConfig("items.heartFragment")
                .bindConfigOptions("heartFragmentRecipe.yml", "options")
                .setRecipeFile("heartFragmentRecipe.yml")
                .reload();
    }

    @Override
    public ItemStack createItem(Player player) {
        return new ItemStack(getRecipeOptions().mat());
    }

    @Override
    public Pair<NamespacedKey, Recipe> createRecipe() {
        unlockOnObtain(getRecipeData().materials());
        return new Pair<>(makeKey("heart_fragment_item_recipe"), getRecipeData().recipe());
    }
}
