package me.ikevoodoo.lssmp.feature.heart;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.lssmp.pipeline.PipelineResult;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipelineHandler;
import me.ikevoodoo.helix.api.config.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class BasicHeartCap implements HeartPipelineHandler {

    private final Configuration generalConfig;

    public BasicHeartCap(Configuration generalConfig) {
        this.generalConfig = generalConfig;
    }

    @Override
    public PipelineResult handle(Player player, @Nullable Entity attacker, AtomicDouble playerHearts, @Nullable AtomicDouble attackerHearts, AtomicBoolean cancel) {
        double minHearts = this.generalConfig.<Double>getValue("minimumHearts") * 2;
        double max = this.generalConfig.getValue("maximumHearts");
        var maxHearts = max < 0 ? Double.MAX_VALUE : max * 2;

        var playerValue = playerHearts.get();
        playerHearts.set(Math.max(minHearts, Math.min(maxHearts, playerValue)));

        if (attackerHearts != null) {
            var attackerValue = attackerHearts.get();
            attackerHearts.set(Math.max(minHearts, Math.min(maxHearts, attackerValue)));
        }

        return PipelineResult.CONTINUE;
    }
}
