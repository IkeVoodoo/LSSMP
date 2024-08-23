package me.ikevoodoo.lssmp.pipeline.heart;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.lssmp.pipeline.PipelineResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeartPipeline {

    private final List<HeartPipelineHandler> handlers = new LinkedList<>();

    private HeartPipeline() {

    }

    public static HeartPipeline create() {
        return new HeartPipeline();
    }

    public void fire(Player player, @Nullable Player killer, AtomicDouble playerHearts, AtomicDouble attackerHearts, AtomicBoolean cancel) {
        var skip = false;
        for (var handler : this.handlers) {
            if (skip) {
                skip = false;
                continue;
            }

            var res = handler.handle(player, killer, playerHearts, attackerHearts, cancel);
            if (res == PipelineResult.CANCEL) return;

            skip = res == PipelineResult.SKIP_NEXT;
        }
    }

    public HeartPipeline andThen(HeartPipelineHandler handler) {
        this.handlers.add(handler);
        return this;
    }

}
