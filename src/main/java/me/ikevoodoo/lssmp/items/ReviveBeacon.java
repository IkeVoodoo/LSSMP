package me.ikevoodoo.lssmp.items;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.callbacks.chat.ChatTransactionListener;
import me.ikevoodoo.smpcore.commands.arguments.parsers.ParserRegistry;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.items.ItemClickResult;
import me.ikevoodoo.smpcore.items.ItemClickState;
import me.ikevoodoo.smpcore.utils.Pair;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

public class ReviveBeacon extends CustomItem {
    public ReviveBeacon(SMPPlugin plugin) {
        super(plugin, "revive_beacon");
        addKey("beacon")
                .setDecreaseOnUse(true)
                //.bindConfig("items.heart")
                //.bindConfigOptions(getPlugin().getConfigHandler().getYmlConfig("heartRecipe.yml").getConfigurationSection("options"))
                .reload();
    }

    @Override
    public ItemStack createItem(Player player) {
        return new ItemStack(Material.BEACON);
    }

    @Override
    public Pair<NamespacedKey, Recipe> createRecipe() {
        ShapelessRecipe recipe = new ShapelessRecipe(makeKey("revive_beacon"), getItemStack());
        recipe.addIngredient(Material.BEACON);
        recipe.addIngredient(Material.GOLD_INGOT);
        return new Pair<>(makeKey("revive_beacon"), recipe);
    }

    @Override
    public ItemClickResult onClick(Player player, ItemStack itemStack, Action action) {
        if(getPlugin().getChatInputHandler().hasListener(player)) {
            return new ItemClickResult(ItemClickState.FAIL, true);
        }

        getPlugin().getChatInputHandler().onChatInput(player, new ChatTransactionListener() {
            @Override
            public boolean onChat(String message) {
                Player plr = ParserRegistry.get(Player.class).parse(player, message);
                if (plr == null) {
                    OfflinePlayer offlinePlayer = ParserRegistry.get(OfflinePlayer.class).parse(player, message);
                    if (!offlinePlayer.hasPlayedBefore()) {
                        player.sendMessage("§cPlayer not found!");
                        return false;
                    }
                    getPlugin().getEliminationHandler().reviveOffline(offlinePlayer);
                    player.sendMessage("§aRevived §e" + offlinePlayer.getName());
                    return true;
                }

                getPlugin().getEliminationHandler().revive(plr);
                player.sendMessage("§aRevived §e" + plr.getDisplayName());
                return true;
            }

            @Override
            public void onComplete(boolean success) {
                if(!success) CustomItem.give(player, getPlugin().getItem("revive_beacon").orElseThrow());
            }
        }, true, "§cEnter the name of the player you want to revive");
        return new ItemClickResult(ItemClickState.SUCCESS, true);
    }
}