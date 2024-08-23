package me.ikevoodoo.lssmp.items;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.helix.api.items.HelixItem;
import me.ikevoodoo.helix.api.items.ItemAction;
import me.ikevoodoo.helix.api.items.callbacks.ItemUseCallback;
import me.ikevoodoo.helix.api.items.callbacks.ItemUseResult;
import me.ikevoodoo.helix.api.items.context.ItemUseContext;
import me.ikevoodoo.helix.api.items.display.ItemDisplayData;
import me.ikevoodoo.helix.api.items.instance.HelixItemInstance;
import me.ikevoodoo.helix.api.items.variables.HelixItemVariables;
import me.ikevoodoo.lssmp.configuration.data.items.custom.HeartItemConfiguration;
import me.ikevoodoo.lssmp.feature.heart.BasicHeartCap;
import me.ikevoodoo.lssmp.feature.heart.BasicHeartGive;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipeline;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class HeartItem extends HelixItem {

    private final HeartItemConfiguration configuration;

    public HeartItem(Configuration heartConfig, HeartItemConfiguration configuration) {
        this.configuration = configuration;
        this.addCallback(ItemAction.RIGHT_CLICK_GENERAL, new ItemUseCallback<>() {
            @Override
            public ItemUseResult onItemUse(ItemUseContext context, HelixItemInstance itemInstance) {
                var player = context.player();
                var attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                assert attribute != null;

                var hearts = new AtomicDouble(attribute.getBaseValue());

                var variables = itemInstance.getVariables();

                double maximumHealth = heartConfig.getValue("maximumHearts");
                if (maximumHealth == -1) {
                    maximumHealth = Double.MAX_VALUE;
                } else {
                    maximumHealth *= 2;
                }

                var maximumClaim = variables.getDouble("max_claim", maximumHealth);
                if (maximumClaim == -1) {
                    maximumClaim = Double.MAX_VALUE;
                } else {
                    maximumClaim *= 2;
                }

                if (hearts.get() >= maximumClaim) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', configuration.getMessages().tooManyHearts()));
                    return ItemUseResult.CANCEL;
                }

                var heartCount = variables.getDouble("heart_count", 1D) * 2D;

                var diff = maximumHealth - hearts.get();
                if (diff <= 0) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', configuration.getMessages().tooManyHearts()));
                    return ItemUseResult.CANCEL;
                }

                if (diff < heartCount && !player.isSneaking()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', configuration.getMessages().partialConsumeAvailable())
                            .replace("{{available_hearts}}", String.valueOf(diff / 2))
                            .replace("{{heart_count}}", String.valueOf(heartCount / 2))
                    );
                    return ItemUseResult.CANCEL;
                }

                var pipeline = HeartPipeline.create()
                        .andThen(new BasicHeartGive(heartCount))
                        .andThen(new BasicHeartCap(heartConfig));

                var healing = variables.getBoolean("healing", true);

                var atMax = player.getHealth() >= hearts.get();

                var originalHearts = hearts.doubleValue();

                pipeline.fire(player, null, hearts, null, new AtomicBoolean());

                var newHearts = hearts.doubleValue();

                attribute.setBaseValue(newHearts);

                if (atMax && healing) {
                    player.setHealth(newHearts);
                }

                var stack = itemInstance.getStack();
                stack.setAmount(stack.getAmount() - 1); // TODO add consume method?

                var claimed = newHearts - originalHearts;

                player.sendMessage(ChatColor.translateAlternateColorCodes('%', configuration.getMessages().successfulUse())
                        .replace("{{heart_count}}", String.valueOf(claimed / 2))
                );

                return ItemUseResult.CANCEL;
            }
        });
    }

    @Override
    public void setupItemStack(ItemStack itemStack, HelixItemVariables variables) {
        variables.setDouble("heart_count", this.configuration.getHeartMultiplier());
        variables.setBoolean("healing", this.configuration.isHealing());
        variables.setBoolean("craftable", this.configuration.isCraftable());
        variables.setString("craftable_text", this.configuration.isCraftable() ? "craftable" : "not craftable");
        variables.setDouble("max_claim", this.configuration.getMaxClaim());
    }

    @Override
    public ItemDisplayData defaultDisplayData() {
        return this.configuration.getDisplayData();
    }
}
