package me.ikevoodoo.lssmp.configuration.data.eliminations;

import me.ikevoodoo.lssmp.configuration.data.types.EliminationNotificationMode;
import me.ikevoodoo.lssmp.configuration.data.types.ReviveHeartsMode;

import java.nio.charset.StandardCharsets;

public record EliminationConfiguration(
        String playerMessage,

        EliminationNotificationMode notificationMode,
        String notificationMessage,

        String permission,

        long banTime,

        ReviveHeartsMode reviveHeartsMode,
        double reviveHearts,

        boolean shouldBanPlayer,
        String[] eliminationCommands,
        String[] reviveCommands
) {

    public static EliminationConfiguration empty() {
        return new EliminationConfiguration(
                "§cYou have been eliminated!",
                EliminationNotificationMode.SUPPRESS,
                null,
                null,
                -1,
                ReviveHeartsMode.USE_DEFAULT_HEARTS,
                -1,

                true,
                new String[0],
                new String[0]
        );
    }

    public byte[] eliminationCommandsAsBytes() {
        return this.stringArrayAsBytes(this.eliminationCommands());
    }

    public byte[] reviveCommandsAsBytes() {
        return this.stringArrayAsBytes(this.reviveCommands);
    }

    private byte[] stringArrayAsBytes(String[] text) {
        return String.join("\0", text).getBytes(StandardCharsets.UTF_8);
    }

}
