package me.ikevoodoo.lssmp.pipeline.heart;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.lssmp.pipeline.PipelineResult;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public interface HeartPipelineHandler {

    PipelineResult handle(Player player, @Nullable Entity attacker, AtomicDouble playerHearts, @Nullable AtomicDouble attackerHearts, AtomicBoolean cancel);

}
