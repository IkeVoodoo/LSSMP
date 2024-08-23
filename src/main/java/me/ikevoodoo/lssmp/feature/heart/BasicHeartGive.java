package me.ikevoodoo.lssmp.feature.heart;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.lssmp.pipeline.PipelineResult;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipelineHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class BasicHeartGive implements HeartPipelineHandler {

    private final double amount;

    public BasicHeartGive(double amount) {
        this.amount = amount;
    }

    @Override
    public PipelineResult handle(Player player, @Nullable Entity attacker, AtomicDouble playerHearts, @Nullable AtomicDouble attackerHearts, AtomicBoolean cancel) {
        playerHearts.addAndGet(this.amount);
        return PipelineResult.CONTINUE;
    }
}
