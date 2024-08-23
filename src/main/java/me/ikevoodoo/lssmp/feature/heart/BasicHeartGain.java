package me.ikevoodoo.lssmp.feature.heart;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.lssmp.configuration.data.types.HeartLossMode;
import me.ikevoodoo.lssmp.pipeline.PipelineResult;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipelineHandler;
import me.ikevoodoo.helix.api.config.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class BasicHeartGain implements HeartPipelineHandler {

    private final Configuration heartConfig;

    public BasicHeartGain(Configuration heartConfig) {
        this.heartConfig = heartConfig;
    }

    @Override
    public PipelineResult handle(Player player, @Nullable Entity attacker, AtomicDouble playerHearts, @Nullable AtomicDouble attackerHearts, AtomicBoolean cancel) {
        var heartLossMode = this.heartConfig.<HeartLossMode>getValue("heartLossMode");

        return switch (heartLossMode) {
            case PLAYERS_ONLY, ALWAYS -> {
                if (attacker == null) yield PipelineResult.CONTINUE;
                assert attackerHearts != null;

                double amount = this.heartConfig.getValue("playerHeartLoss");
                attackerHearts.addAndGet(amount * 2);

                yield PipelineResult.CONTINUE;
            }

            case ENVIRONMENT_ONLY, NEVER -> PipelineResult.CONTINUE;
        };
    }
}
