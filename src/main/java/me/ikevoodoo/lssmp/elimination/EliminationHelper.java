package me.ikevoodoo.lssmp.elimination;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.storage.HelixDataStorage;
import me.ikevoodoo.lssmp.configuration.data.eliminations.EliminationConfiguration;
import me.ikevoodoo.lssmp.configuration.data.types.EliminationNotificationMode;
import me.ikevoodoo.lssmp.configuration.data.types.ReviveHeartsMode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class EliminationHelper {

    public static int getHighestConfiguration(Permissible permissible, EliminationConfiguration[] configurations) {
        int highest = -1;

        for (int i = 0; i < configurations.length; i++) {
            var config = configurations[i];
            if (config.permission() == null || permissible.hasPermission(config.permission())) {
                highest = i;
            }
        }

        return highest;
    }

    public static EliminationInfo getInfoFor(Player player, EliminationConfiguration[] configurations, HelixDataStorage storage) {
        var highest = getHighestConfiguration(player, configurations);

        final var configuration = (highest == -1)
                ? EliminationConfiguration.empty()
                : configurations[highest];

        return new EliminationInfo(player, getKiller(storage), configuration, System.currentTimeMillis());
    }

    public static EliminationInfo fromStorage(UUID uuid, HelixDataStorage storage) {
        final var eliminationCommands = new String(storage.getByteArray("eliminationCommands"), StandardCharsets.UTF_8).split("\0");
        final var reviveCommands = new String(storage.getByteArray("reviveCommands"), StandardCharsets.UTF_8).split("\0");

        return new EliminationInfo(
                Bukkit.getOfflinePlayer(uuid),
                getKiller(storage),
                new EliminationConfiguration(
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
                ),
                storage.getLong("eliminatedAt")
        );
    }

    public static boolean revive(OfflinePlayer player, Player reviver) {
        final var eliminatedId = player.getUniqueId();

        var tag = Helix.tags().get("elimination");
        if (tag.has(eliminatedId)) {
            return false;
        }

        if (!player.isOnline()) {
            tag.editData(eliminatedId, storage -> {
                storage.setLong("banTime", 0);
                storage.setString("reviver", reviver == null ? "[ENVIRONMENT]" : reviver.getName());
            });
            return true;
        }

        tag.remove(eliminatedId);

        final var data = EliminationHelper.fromStorage(eliminatedId, tag.getData(eliminatedId));

        final var reviverName = reviver == null ? "[ENVIRONMENT]" : reviver.getName();

        for (final var command : data.configuration().reviveCommands()) {
            if (command.isBlank()) continue;

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), data.formatMessage(command).replace("{{reviver}}", reviverName));
        }

        return true;
    }

    public static void eliminate(OfflinePlayer player, Player attacker) {
        var tag = Helix.tags().get("elimination");
        tag.add(player.getUniqueId(), (uuid, storage) ->
                storage.setString("killer", attacker == null ? "Environment" : attacker.getUniqueId().toString()));

        var storage = tag.getData(player.getUniqueId());
        var data = EliminationHelper.fromStorage(player.getUniqueId(), storage);

        for (final var command : data.configuration().eliminationCommands()) {
            if (command.isBlank()) continue;

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), data.formatMessage(command));
        }

        if (player.isOnline()) {
            processOlineElimination(data, player.getPlayer(), attacker);
        }
    }

    private static void processOlineElimination(EliminationInfo data, @Nullable Player player, Player attacker) {
        assert player != null;

        player.setFallDistance(0);

        if (data.configuration().shouldBanPlayer()) {
            player.kickPlayer(data.getKickMessage(player));
        }

        final var notificationMessage = data.configuration().notificationMessage();

        switch (data.configuration().notificationMode()) {
            case SEND_TO_KILLER -> {
                if (attacker != null) {
                    attacker.sendMessage(data.formatMessage(notificationMessage));
                }
            }
            case SEND_TO_EVERYONE -> Bukkit.broadcastMessage(data.formatMessage(notificationMessage));
        }
    }

    private static OfflinePlayer getKiller(HelixDataStorage storage) {
        var killer = storage.getString("killer");

        if (killer.equalsIgnoreCase("Environment")) {
            return null;
        }

        return Bukkit.getOfflinePlayer(UUID.fromString(killer));
    }

}
