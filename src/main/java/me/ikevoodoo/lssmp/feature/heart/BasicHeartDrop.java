package me.ikevoodoo.lssmp.feature.heart;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.helix.api.namespaced.UniqueIdentifier;
import me.ikevoodoo.lssmp.Constants;
import me.ikevoodoo.lssmp.configuration.data.types.PlayerDropHeartsMode;
import me.ikevoodoo.lssmp.pipeline.PipelineResult;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipelineHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class BasicHeartDrop implements HeartPipelineHandler {

    private final Configuration combatConfig;
    private final Configuration heartConfig;

    public BasicHeartDrop(Configuration combatConfig, Configuration heartConfig) {
        this.combatConfig = combatConfig;
        this.heartConfig = heartConfig;
    }

    @Override
    public PipelineResult handle(Player player, @Nullable Entity attacker, AtomicDouble playerHearts, @Nullable AtomicDouble attackerHearts, AtomicBoolean cancel) {
        var mode = this.combatConfig.<PlayerDropHeartsMode>getValue("playerDropHeartsMode");

        return switch (mode) {
            case ALWAYS -> {
                dropHeart(player);
                yield PipelineResult.SKIP_NEXT;
            }
            case MAX_HEARTS_ONLY -> {
                if (attacker == null) {
                    yield PipelineResult.CONTINUE;
                }
                assert attackerHearts != null;

                double max = this.heartConfig.getValue("maximumHearts");

                if (attackerHearts.get() < max * 2) {
                    yield PipelineResult.CONTINUE;
                }

                dropHeart(player);
                yield PipelineResult.SKIP_NEXT;
            }
            case ENVIRONMENT_KILLS_ONLY -> {
                if (attacker != null) {
                    yield PipelineResult.CONTINUE;
                }
                
                dropHeart(player);
                yield PipelineResult.SKIP_NEXT;
            }
            case PLAYER_KILLS_ONLY -> {
                if (attacker == null) {
                    yield PipelineResult.CONTINUE;
                }

                dropHeart(player);
                yield PipelineResult.SKIP_NEXT;
            }
            case NEVER -> PipelineResult.CONTINUE;
        };
    }

    private void dropHeart(Player player) {
        var uid = UniqueIdentifier.combine(Constants.PLUGIN_KEY, this.combatConfig.getValue("heartToDrop"));

        var customItem = Helix.items().getItem(uid);

        var stack = Helix.items().createItem(uid, customItem.defaultDisplayData());

        final var playerLocation = player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        var world = player.getWorld();

        final var droppedHeartsFloat = this.combatConfig.<Boolean>getValue("droppedHeartsFloat");
        if (!droppedHeartsFloat) {
            world.dropItemNaturally(playerLocation, stack, item -> {
                item.setInvulnerable(true);
                item.setGlowing(true);
            });
            return;
        }

        final var location = playerLocation.add(0, 2, 0);
        if (!location.getBlock().getType().isAir()) {
            location.subtract(0, 1, 0); // Try to place the heart item at the feet instead of the eyes
        }

        while (!location.getBlock().getType().isAir()) {
            location.add(0, 1, 0);
        }

        world.spawn(location, Item.class, item -> {
            item.setGravity(false);
            item.setInvulnerable(true);
            item.setGlowing(true);
            item.setVelocity(new Vector());

            item.setItemStack(stack);
        });
    }
}
