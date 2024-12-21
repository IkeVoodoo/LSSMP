package me.ikevoodoo.lssmp.elimination;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.messages.colors.MinecraftColor;
import me.ikevoodoo.helix.api.storage.HelixDataStorage;
import me.ikevoodoo.lssmp.configuration.data.eliminations.EliminationConfiguration;
import me.ikevoodoo.lssmp.time.TimeFormatter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public record EliminationInfo(OfflinePlayer victim, @Nullable OfflinePlayer killer, EliminationConfiguration configuration, long eliminatedAt) {

    public EliminationInfo(OfflinePlayer player, @Nullable OfflinePlayer killer, EliminationConfiguration configuration) {
        this(player, killer, configuration, System.currentTimeMillis());
    }

    public static EliminationInfo fromStorage(UUID player, HelixDataStorage storage) {
        final var killerId = storage.getString("killer");
        final var killerIdValid = killerId != null && !"Environment".equals(killerId);
        final var killer = killerIdValid ? Helix.players().getOffline(killerId) : null;

        return new EliminationInfo(
                Helix.players().getOffline(player),
                killer,
                EliminationConfiguration.fromStorage(storage),
                storage.getLong("eliminatedAt")
        );
    }

    public Player onlineVictim() {
        return this.victim.getPlayer();
    }

    public boolean hasKiller() {
        return this.killer != null;
    }

    public Player onlineKiller() {
        if (!this.hasKiller()) {
            return null;
        }

        return this.killer.getPlayer();
    }

    public void initializePlayerData(UUID playerId, HelixDataStorage data) {
        this.editPlayerData(data);
    }

    public void editPlayerData(HelixDataStorage data) {
        data.setString("killer", this.killer == null ? "[ENVIRONMENT]" : this.killer.getUniqueId().toString());
        data.setLong("eliminatedAt", this.eliminatedAt);
        this.configuration.editPlayerData(data);
    }

    public long getPardonAt() {
        var banTime = this.configuration.banTime();

        return banTime == Long.MAX_VALUE ? Long.MAX_VALUE : this.eliminatedAt() + banTime;
    }

    public String getKickMessage(InetAddress address) {
        var now = System.currentTimeMillis();
        var banTime = this.configuration.banTime();

        var pardonAt = getPardonAt();

        var remainingSeconds = (pardonAt - now) / 1_000;

        var info = Helix.players().getInformation(address); // I know this blocks

        var killerName = this.killer == null ? "Environment" : this.killer.getName();

        return MinecraftColor.replaceColorCodes('&', this.configuration().playerMessage())
                .replace("{{time_remaining}}", banTime == Long.MAX_VALUE ? "infinite" : TimeFormatter.formatDuration(remainingSeconds))
                .replace("{{revived_at}}", banTime == Long.MAX_VALUE ? "the end of time" : TimeFormatter.formatDate(
                        LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(
                                        pardonAt
                                ),
                                info == null ? ZoneId.systemDefault() : info.timezone().toZoneId()
                        ),
                        info != null && info.usesBackwardsDate()
                ))
                .replace("{{killer}}", killerName == null ? "Unknown" : killerName);
    }

    public String formatMessage(@NotNull String message) {
        final var playerName = this.victim.getName();
        final var killerName = this.killer == null
                ? "[ENVIRONMENT]"
                : this.killer.getName();

        return message
                .replace("{{player}}", playerName == null ? "[UNKNOWN]" : playerName)
                .replace("{{killer}}", killerName == null ? "[UNKNOWN]" : killerName);
    }

    public String getKickMessage(Player player) {
        var address = player.getAddress();
        if (address == null) return null;

        return this.getKickMessage(address.getAddress());
    }

    @SuppressWarnings("unused")
    public String getKickMessage() {
        var player = this.victim.getPlayer();
        if (player == null) return null;

        return this.getKickMessage(player);
    }

}
