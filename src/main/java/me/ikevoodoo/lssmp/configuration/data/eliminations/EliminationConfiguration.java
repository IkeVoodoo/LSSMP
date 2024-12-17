package me.ikevoodoo.lssmp.configuration.data.eliminations;

import me.ikevoodoo.lssmp.configuration.data.types.EliminationNotificationMode;
import me.ikevoodoo.lssmp.configuration.data.types.ReviveHeartsMode;

public record EliminationConfiguration(
        String playerMessage,

        EliminationNotificationMode notificationMode,
        String notificationMessage,

        String permission,

        long banTime,

        ReviveHeartsMode reviveHeartsMode,
        double reviveHearts,

        boolean shouldBanPlayer,
        String[] eliminationCommands
) {


}
