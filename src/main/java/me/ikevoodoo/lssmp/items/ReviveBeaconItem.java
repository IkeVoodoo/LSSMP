package me.ikevoodoo.lssmp.items;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.items.HelixItem;
import me.ikevoodoo.helix.api.items.ItemAction;
import me.ikevoodoo.helix.api.items.callbacks.ItemUseCallback;
import me.ikevoodoo.helix.api.items.callbacks.ItemUseResult;
import me.ikevoodoo.helix.api.items.context.ItemUseContext;
import me.ikevoodoo.helix.api.items.display.ItemDisplayData;
import me.ikevoodoo.helix.api.items.instance.HelixItemInstance;
import me.ikevoodoo.helix.api.items.variables.HelixItemVariables;
import me.ikevoodoo.lssmp.configuration.data.items.custom.ReviveBeaconConfiguration;
import org.bukkit.inventory.ItemStack;

import static me.ikevoodoo.lssmp.Constants.REVIVE_SCREEN_ID;

public class ReviveBeaconItem extends HelixItem {

    private final ReviveBeaconConfiguration configuration;

    public ReviveBeaconItem(ReviveBeaconConfiguration configuration) {
        this.configuration = configuration;

        this.addCallback(ItemAction.RIGHT_CLICK_GENERAL, new ItemUseCallback<>() {
            @Override
            public ItemUseResult onItemUse(ItemUseContext context, HelixItemInstance itemInstance) {
                var res = Helix.screens().open(context.player(), REVIVE_SCREEN_ID, null);
                var selection = res.component("selection");
                assert selection != null;

                var variables = itemInstance.getVariables();

                selection.setProperty("heart_cost", variables.getInt("heart_cost"));
//                selection.setProperty("max_revives", variables.getInt("max_revives"));
                selection.setProperty("item_id", configuration.getId());

                if (!res.success()) {
                    context.player().sendMessage("§cUnable to open the screen! Please contact server admins.");
                    return ItemUseResult.CANCEL;
                }

                return ItemUseResult.SUCCEED;
            }
        });
    }

    @Override
    public void setupItemStack(ItemStack stack, HelixItemVariables variables) {
//        variables.setInt("max_revives", this.configuration.getMaxRevives());
        variables.setInt("heart_cost", this.configuration.getHeartCost());
    }

    @Override
    public ItemDisplayData defaultDisplayData() {
        return this.configuration.getDisplayData();
    }
}
