package me.ikevoodoo.lssmp.items;

import me.ikevoodoo.helix.api.items.HelixItem;
import me.ikevoodoo.helix.api.items.ItemAction;
import me.ikevoodoo.helix.api.items.callbacks.ItemUseCallback;
import me.ikevoodoo.helix.api.items.callbacks.ItemUseResult;
import me.ikevoodoo.helix.api.items.context.ItemUseContext;
import me.ikevoodoo.helix.api.items.display.ItemDisplayData;
import me.ikevoodoo.helix.api.items.display.ItemTextDisplayData;
import me.ikevoodoo.helix.api.items.instance.HelixItemInstance;
import me.ikevoodoo.helix.api.items.variables.HelixItemVariables;
import me.ikevoodoo.helix.api.messages.MessageBuilder;
import me.ikevoodoo.helix.api.messages.colors.MinecraftColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class BaconItem extends HelixItem {

    public BaconItem() {
        this.addCallback(ItemAction.RIGHT_CLICK_GENERAL, new ItemUseCallback<>() {
            @Override
            public ItemUseResult onItemUse(ItemUseContext context, HelixItemInstance itemInstance) {
                context.player().sendMessage(MinecraftColor.fromHex("#fa93ee") + "Hmm... YUMMY!!!");
                context.player().addPotionEffect(new PotionEffect(
                        PotionEffectType.REGENERATION,
                        20 * 8,
                        0,
                        true,
                        true,
                        true
                ));
                context.player().playEffect(EntityEffect.LOVE_HEARTS);
                context.player().setVelocity(new Vector(0, 1, 0));

                var stack = itemInstance.getStack();
                stack.setAmount(stack.getAmount() - 1); // TODO add consume method?

                return ItemUseResult.SUCCEED;
            }
        });
    }

    @Override
    public void setupItemStack(ItemStack stack, HelixItemVariables variables) {

    }

    @Override
    public ItemDisplayData defaultDisplayData() {
        return new ItemDisplayData(
                Material.COOKED_PORKCHOP,
                900,
                new ItemTextDisplayData(
                        new MessageBuilder()
                                .literal("Bacon")
                                .color(MinecraftColor.fromHex("#fa93ee"))
                                .bold(true)
                                .build()
                                .toLegacyText(),
                        List.of(
                                new MessageBuilder()
                                        .literal("A little birdie told me... that you wasted resources.")
                                        .color(MinecraftColor.GRAY)
                                        .italic(true)
                                        .build()
                                        .toLegacyText()
                        )
                )
        );
    }
}
