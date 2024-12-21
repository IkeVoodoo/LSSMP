package me.ikevoodoo.lssmp.elimination.result;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record EliminationAsyncJoinResult(boolean removeTag, @Nullable String kickMessage) {

    @SuppressWarnings("unused")
    public static EliminationAsyncJoinResult noAction() {
        return new EliminationAsyncJoinResult(false, null);
    }

    public static EliminationAsyncJoinResult kick(@NotNull String kickMessage) {
        return new EliminationAsyncJoinResult(false, kickMessage);
    }

    public static EliminationAsyncJoinResult revive() {
        return new EliminationAsyncJoinResult(true, null);
    }

}
