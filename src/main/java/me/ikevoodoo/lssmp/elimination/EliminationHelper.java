package me.ikevoodoo.lssmp.elimination;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.storage.HelixDataStorage;
import me.ikevoodoo.lssmp.configuration.data.eliminations.EliminationConfiguration;
import me.ikevoodoo.lssmp.configuration.data.eliminations.EliminationConfigurations;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EliminationHelper {

    public static EliminationInfo getInfoFor(Player player, EliminationConfiguration[] configurationArray, HelixDataStorage storage) {
        final var configurations = new EliminationConfigurations(configurationArray);

        return new EliminationInfo(player, getKiller(storage), configurations.findHighestConfiguration(player));
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

        final var data = EliminationInfo.fromStorage(eliminatedId, tag.getData(eliminatedId));

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
        var data = EliminationInfo.fromStorage(player.getUniqueId(), storage);

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
