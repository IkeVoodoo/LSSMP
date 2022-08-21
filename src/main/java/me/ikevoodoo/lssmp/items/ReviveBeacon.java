package me.ikevoodoo.lssmp.items;

import me.ikevoodoo.lssmp.config.ItemConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.callbacks.chat.ChatTransactionListener;
import me.ikevoodoo.smpcore.commands.arguments.parsers.ParserRegistry;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.items.ItemClickResult;
import me.ikevoodoo.smpcore.items.ItemClickState;
import me.ikevoodoo.smpcore.text.messaging.MessageBuilder;
import me.ikevoodoo.smpcore.utils.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class ReviveBeacon extends CustomItem {
    public ReviveBeacon(SMPPlugin plugin) {
        super(plugin, "revive_beacon", MessageBuilder.messageOf("§c§lRevive Beacon"));
        addKey("beacon")
                .setDecreaseOnUse(true)
                .bindConfig("items.beacon")
                .bindConfigOptions("beaconRecipe.yml", "options")
                .setRecipeFile("beaconRecipe.yml")
                .reload();
    }

    @Override
    public ItemStack createItem(Player player) {
        return new ItemStack(getRecipeOptions().mat());
    }

    @Override
    public Pair<NamespacedKey, Recipe> createRecipe() {
        unlockOnObtain(getRecipeData().materials());
        return new Pair<>(makeKey("revive_beacon_recipe"), getRecipeData().recipe());
    }
/*
    @Override
    public RecipeData createRecipeData() {
        return getPlugin().getRecipeLoader().getRecipe(
                getPlugin().getConfigHandler().getYmlConfig("beaconRecipe.yml"),
                "recipe", getItemStack(),
                makeKey("revive_beacon"),
                getRecipeOptions()
        );
    }*/

    @Override
    public ItemClickResult onClick(Player player, ItemStack itemStack, Action action) {
        if(getPlugin().getChatInputHandler().hasListener(player)) {
            return new ItemClickResult(ItemClickState.FAIL, true);
        }

        if (true) {
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, .5f, 2);
            getPlugin().getMenuHandler().get("lssmp_revive_beacon_menu").open(player);
            return new ItemClickResult(ItemClickState.IGNORE, true);
        }

        getPlugin().getChatInputHandler().onCancellableInput(player, new ChatTransactionListener() {
            @Override
            public boolean onChat(String message) {
                Player plr = ParserRegistry.get(Player.class).parse(player, message);
                if (plr == null) {
                    OfflinePlayer offlinePlayer = ParserRegistry.get(OfflinePlayer.class).parse(player, message);
                    if (!offlinePlayer.hasPlayedBefore()) {
                        player.sendMessage(MainConfig.Messages.Errors.notFound.replace("%s", "Player"));
                        return false;
                    }
                    getPlugin().getEliminationHandler().reviveOffline(offlinePlayer);
                    player.sendMessage(ItemConfig.ReviveBeacon.Messages.revivedPlayer.replace("%s", "" + offlinePlayer.getName()));
                    return true;
                }

                getPlugin().getEliminationHandler().revive(plr);
                player.sendMessage(ItemConfig.ReviveBeacon.Messages.revivedPlayer.replace("%s", plr.getDisplayName()));
                return true;
            }

            @Override
            public void onComplete(boolean success) {
                if(!success) CustomItem.give(player, getPlugin().getItem("revive_beacon").orElseThrow());
            }
        }, ItemConfig.ReviveBeacon.Messages.cancelMessage, ItemConfig.ReviveBeacon.Messages.cancelled, ItemConfig.ReviveBeacon.Messages.useMessage);
        return new ItemClickResult(ItemClickState.SUCCESS, true);
    }
}