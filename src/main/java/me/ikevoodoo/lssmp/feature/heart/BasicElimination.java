package me.ikevoodoo.lssmp.feature.heart;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.lssmp.pipeline.PipelineResult;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipelineHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class BasicElimination implements HeartPipelineHandler {

    private final Configuration generalConfig;

    public BasicElimination(Configuration generalConfig) {
        this.generalConfig = generalConfig;
    }

    @Override
    public PipelineResult handle(Player player, @Nullable Entity attacker, AtomicDouble playerHearts, @Nullable AtomicDouble attackerHearts, AtomicBoolean cancel) {
        double minHearts = this.generalConfig.<Double>getValue("minimumHearts") * 2;
        var eliminate = this.generalConfig.<Boolean>getValue("eliminatePlayers");

        if (eliminate && playerHearts.get() <= minHearts) {
            playerHearts.set(-1);
            cancel.set(true);
        }

        return PipelineResult.CANCEL;
    }
}
