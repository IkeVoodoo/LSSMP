package me.ikevoodoo.lssmp.feature.heart;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.lssmp.configuration.data.types.HeartLossMode;
import me.ikevoodoo.lssmp.pipeline.PipelineResult;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipelineHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class BasicHeartLoss implements HeartPipelineHandler {

    private final Configuration heartConfig;

    public BasicHeartLoss(Configuration heartConfig) {
        this.heartConfig = heartConfig;
    }

    @Override
    public PipelineResult handle(Player player, @Nullable Entity attacker, AtomicDouble playerHearts, @Nullable AtomicDouble attackerHearts, AtomicBoolean cancel) {
        var heartLossMode = this.heartConfig.<HeartLossMode>getValue("heartLossMode");
        return switch (heartLossMode) {
            case ALWAYS -> {
                double amount = attacker == null
                        ? this.heartConfig.getValue("environmentHeartLoss")
                        : this.heartConfig.getValue("playerHeartLoss");

                playerHearts.addAndGet(-(amount * 2));

                yield PipelineResult.CONTINUE;
            }

            case PLAYERS_ONLY -> {
                if (attacker == null) yield PipelineResult.CANCEL;

                double amount = this.heartConfig.getValue("playerHeartLoss");
                playerHearts.addAndGet(-(amount * 2));

                yield PipelineResult.CONTINUE;
            }

            case ENVIRONMENT_ONLY -> {
                if (attacker != null) yield PipelineResult.CANCEL;

                double amount = this.heartConfig.getValue("environmentHeartLoss");
                playerHearts.addAndGet(-(amount * 2));

                yield PipelineResult.CONTINUE;
            }

            case NEVER -> PipelineResult.CANCEL;
        };
    }
}
