package me.ikevoodoo.lssmp.configuration;


import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.lssmp.configuration.data.eliminations.EliminationConfiguration;
import me.ikevoodoo.lssmp.configuration.data.types.EliminationNotificationMode;
import me.ikevoodoo.lssmp.configuration.data.types.HeartLossMode;
import me.ikevoodoo.lssmp.configuration.data.types.PlayerDropHeartsMode;
import me.ikevoodoo.lssmp.configuration.data.types.ReviveHeartsMode;
import me.ikevoodoo.lssmp.configuration.data.types.TotemUseMode;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ConfigurationConverter {

    public static void convertMain(YamlConfiguration oldConfig, Configuration config) {
        var elimination = oldConfig.getConfigurationSection("elimination");
        if (elimination == null) return;

        { // Combat
            var combat = config.child("combat");

            var alwaysDrop = elimination.getBoolean("alwaysDropHearts", false);
            var playersDrop = elimination.getBoolean("playersDropHearts", true);

            var mode = alwaysDrop
                    ? PlayerDropHeartsMode.ALWAYS
                    : playersDrop
                        ? PlayerDropHeartsMode.PLAYER_KILLS_ONLY
                        : PlayerDropHeartsMode.MAX_HEARTS_ONLY;

            combat.value("playerDropHeartsMode").value(mode);

            var environmentSteals = elimination.getBoolean("environmentStealsHearts", true);

            var heartLossMode = environmentSteals ? HeartLossMode.ALWAYS : HeartLossMode.PLAYERS_ONLY;

            var loss = combat.child("heartLoss");
            loss.value("heartLossMode").value(heartLossMode);
            loss.value("environmentHeartLoss").value(elimination.getDouble("environmentHealthScale", 1.0));
            loss.value("playerHealthLoss").value(elimination.getDouble("healthScale", 1.0));

            combat.value("totemUseMode").value(elimination.getBoolean("totemWorksInInventory") ? TotemUseMode.ALWAYS : TotemUseMode.IN_HAND_ONLY);
        }

        { // General
            var general = config.child("general");

            general.value("defaultHearts").value(elimination.getDouble("defaultHearts", 10.0));

            if (elimination.getBoolean("useMinHealth")) {
                general.value("minimumHearts").value(elimination.getDouble("minHearts", 0.0));
            }

            var max = elimination.getDouble("maxHearts", 20.0);
            var useMax = elimination.getBoolean("useMaxHealth", true);

            general.value("maximumHearts").value(useMax ? max : -1);

            general.value("eliminatePlayers").value(elimination.getBoolean("banAtMinHealth", true));
        }
    }

    public static void convertBans(YamlConfiguration oldConfig, Configuration config) {
        var banTimes = oldConfig.getConfigurationSection("banTimes");
        if (banTimes == null) return;

        var configs = new ArrayList<EliminationConfiguration>();

        var keys = banTimes.getKeys(false);
        for (var key : keys) {
            var section = banTimes.getConfigurationSection(key);
            if (section == null) continue;

            var kickMessage = section.getString("ban-message", "§cYou have been eliminated!");
            var notificationMode = section.getBoolean("broadcast-ban", true)
                    ? EliminationNotificationMode.SEND_TO_EVERYONE
                    : EliminationNotificationMode.SUPPRESS;
            var notificationMessage = section.getString("broadcast-ban-message", "§c{{player}} §6has been eliminated!");
            var permission = section.getString("permission", null);
            var permanentBan = section.getBoolean("permanentBan", true);
            var banTime = permanentBan ? -1 : parseOldBanTime(section.getString("time", "24:00:00.0000"));

            var info = new EliminationConfiguration(
                    kickMessage,
                    notificationMode,
                    notificationMessage,
                    permission,
                    banTime,

                    ReviveHeartsMode.USE_REVIVE_HEARTS,
                    10.0
            );

            configs.add(info);
        }

        config.compoundArray("eliminations").values(configs.toArray(EliminationConfiguration[]::new));
    }

    private static long parseOldBanTime(String old) {
        var times = old.split("[:.]");

        var hours =        times.length >  1 ? Integer.parseInt(times[0]) : 0;
        var minutes =      times.length >= 2 ? Integer.parseInt(times[1]) : 0;
        var seconds =      times.length >= 3 ? Integer.parseInt(times[2]) : 0;
        var milliseconds = times.length >= 4 ? Integer.parseInt(times[3]) : 0;

        return TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes)
                + TimeUnit.SECONDS.toMillis(seconds) + milliseconds;
    }

}
