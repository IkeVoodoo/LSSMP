package me.ikevoodoo.lssmp.configuration.data.eliminations;

import me.ikevoodoo.helix.api.storage.HelixDataStorage;
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

        String eliminationMode
) {

    public static EliminationConfiguration empty() {
        return new EliminationConfiguration(
                "§cYou have been eliminated!",
                EliminationNotificationMode.SUPPRESS,
                "",
                null,
                -1,
                ReviveHeartsMode.USE_DEFAULT_HEARTS,
                -1,

                "ban_player"
        );
    }

    public static EliminationConfiguration fromStorage(HelixDataStorage storage) {
        return new EliminationConfiguration(
                storage.getString("playerMessage"),
                EliminationNotificationMode.values()[storage.getByte("notifMode")],
                storage.getString("notifMsg"),
                null,
                storage.getLong("banTime"),
                ReviveHeartsMode.values()[storage.getByte("reviveMode")],
                storage.getDouble("reviveHearts"),

                storage.getString("eliminationMode", "ban_player")
        );
    }

    public void editPlayerData(HelixDataStorage data) {
        data.setLong("banTime", this.banTime < 0 ? Long.MAX_VALUE : this.banTime);
        data.setString("playerMessage", this.playerMessage);
        data.setByte("notifMode", (byte) this.notificationMode.ordinal());
        data.setString("notifMsg", this.notificationMessage);
        data.setByte("reviveMode", (byte) this.reviveHeartsMode.ordinal());
        data.setDouble("reviveHearts", this.reviveHearts);
        data.setString("eliminationMode", this.eliminationMode);
    }

    public void removeBanTime(HelixDataStorage data) {
        data.setLong("banTime", 0L);
    }

}
