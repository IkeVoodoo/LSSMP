package me.ikevoodoo.lssmp.elimination;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.messages.colors.MinecraftColor;
import me.ikevoodoo.lssmp.configuration.data.eliminations.EliminationConfiguration;
import me.ikevoodoo.lssmp.time.TimeFormatter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record EliminationInfo(OfflinePlayer player, @Nullable OfflinePlayer killer, EliminationConfiguration configuration, long eliminatedAt) {

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
                .replace("{{revived_at}}", banTime == Long.MAX_VALUE ? "the end of the universe" : TimeFormatter.formatDate(
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

    public String getNotificationMessage(Player player, @Nullable Player killer) {
        var plain = this.configuration.notificationMessage();

        plain = plain.replace("{{player}}", player.getName());

        if (killer != null) {
            plain = plain.replace("{{killer}}", killer.getName());
        } else {
            plain = plain.replace("{{killer}}", "Environment");
        }

        return plain;
    }

    public String getKickMessage(Player player) {
        var addr = player.getAddress();
        if (addr == null) return null;

        return this.getKickMessage(addr.getAddress());
    }

    public String getKickMessage() {
        var player = this.player.getPlayer();
        if (player == null) return null;

        return this.getKickMessage(player);
    }

}
