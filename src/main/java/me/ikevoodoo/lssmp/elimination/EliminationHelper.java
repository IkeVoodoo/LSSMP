package me.ikevoodoo.lssmp.elimination;

import me.ikevoodoo.helix.api.storage.HelixDataStorage;
import me.ikevoodoo.lssmp.configuration.data.eliminations.EliminationConfiguration;
import me.ikevoodoo.lssmp.configuration.data.types.EliminationNotificationMode;
import me.ikevoodoo.lssmp.configuration.data.types.ReviveHeartsMode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.UUID;

public class EliminationHelper {

    public static int getHighestConfiguration(Permissible permissible, EliminationConfiguration[] configurations) {
        int highest = -1;

        for (int i = 0; i < configurations.length; i++) {
            var config = configurations[i];
            if (config.permission() != null && permissible.hasPermission(config.permission())) {
                highest = i;
            }
        }

        return highest;
    }

    public static EliminationInfo getInfoFor(Player player, EliminationConfiguration[] configurations, HelixDataStorage storage) {
        var highest = getHighestConfiguration(player, configurations);

        EliminationConfiguration configuration;
        if (highest == -1) {
            configuration = new EliminationConfiguration(
                    "§cYou have been eliminated!",
                    EliminationNotificationMode.SUPPRESS,
                    "",
                    "",
                    -1,
                    ReviveHeartsMode.USE_DEFAULT_HEARTS,
                    -1
            );
        } else {
            configuration = configurations[highest];
        }

        return new EliminationInfo(player, getKiller(storage), configuration, System.currentTimeMillis());
    }

    public static EliminationInfo fromStorage(UUID uuid, HelixDataStorage storage) {
        return new EliminationInfo(
                Bukkit.getOfflinePlayer(uuid),
                getKiller(storage),
                new EliminationConfiguration(
                        storage.getString("playerMessage"),
                        EliminationNotificationMode.values()[storage.getByte("notifMode")],
                        storage.getString("notifMsg"),
                        "",
                        storage.getLong("banTime"),
                        ReviveHeartsMode.values()[storage.getByte("reviveMode")],
                        storage.getDouble("reviveHearts")
                ),
                storage.getLong("eliminatedAt")
        );
    }

    private static OfflinePlayer getKiller(HelixDataStorage storage) {
        var killer = storage.getString("killer");

        if (killer.equalsIgnoreCase("Environment")) {
            return null;
        }

        return Bukkit.getOfflinePlayer(UUID.fromString(killer));
    }

}
