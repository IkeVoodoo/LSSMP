package me.ikevoodoo.lssmp.elimination.modes;

import me.ikevoodoo.lssmp.elimination.context.EliminationContext;
import me.ikevoodoo.lssmp.elimination.result.EliminationAsyncJoinResult;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.UUID;

public interface EliminationMode {

    String id();

    /**
     * @return If this elimination mode supports attempted eliminations
     * */
    boolean onAttemptElimination(@NotNull Player victim, @Nullable Player killer, @NotNull EliminationContext ctx);

    void onEliminated(@NotNull Player victim, @Nullable Player killer, @NotNull EliminationContext ctx);

    void onRevived(@NotNull OfflinePlayer revived, @Nullable Player reviver, @NotNull EliminationContext ctx);

    EliminationAsyncJoinResult onAsyncJoin(@NotNull UUID eliminatedPlayer, @NotNull InetAddress address, @NotNull EliminationContext ctx);

    void onJoin(@NotNull Player eliminatedPlayer, @NotNull EliminationContext ctx);

    void onQuit(@NotNull Player eliminatedPlayer, @NotNull EliminationContext ctx);

}
