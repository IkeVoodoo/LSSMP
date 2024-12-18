package me.ikevoodoo.lssmp.configuration.data.eliminations;

import me.ikevoodoo.helix.api.storage.HelixDataStorage;
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

    public static EliminationConfiguration fromStorage(HelixDataStorage storage) {
        final var eliminationCommands = new String(storage.getByteArray("eliminationCommands"), StandardCharsets.UTF_8).split("\0");
        final var reviveCommands = new String(storage.getByteArray("reviveCommands"), StandardCharsets.UTF_8).split("\0");

        return new EliminationConfiguration(
                storage.getString("playerMessage"),
                EliminationNotificationMode.values()[storage.getByte("notifMode")],
                storage.getString("notifMsg"),
                null,
                storage.getLong("banTime"),
                ReviveHeartsMode.values()[storage.getByte("reviveMode")],
                storage.getDouble("reviveHearts"),

                storage.getBoolean("shouldBanPlayer"),
                eliminationCommands,
                reviveCommands
        );
    }

    public void editPlayerData(HelixDataStorage data) {
        data.setLong("banTime", this.banTime < 0 ? Long.MAX_VALUE : this.banTime);
        data.setString("playerMessage", this.playerMessage);
        data.setByte("notifMode", (byte) this.notificationMode.ordinal());
        data.setString("notifMsg", this.notificationMessage);
        data.setByte("reviveMode", (byte) this.reviveHeartsMode.ordinal());
        data.setDouble("reviveHearts", this.reviveHearts);
        data.setByteArray("eliminationCommands", this.eliminationCommandsAsBytes());
        data.setByteArray("reviveCommands", this.reviveCommandsAsBytes());
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
