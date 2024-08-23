package me.ikevoodoo.lssmp.configuration.data.eliminations;

import me.ikevoodoo.lssmp.configuration.data.types.EliminationNotificationMode;
import me.ikevoodoo.lssmp.configuration.data.types.ReviveHeartsMode;
import me.ikevoodoo.lssmp.time.TimeFormatter;

public record EliminationConfiguration(
        String playerMessage,

        EliminationNotificationMode notificationMode,
        String notificationMessage,

        String permission,

        long banTime,

        ReviveHeartsMode reviveHeartsMode,
        double reviveHearts
) {

    public EliminationConfiguration {
        System.out.println(TimeFormatter.formatDuration(banTime));
    }

}
