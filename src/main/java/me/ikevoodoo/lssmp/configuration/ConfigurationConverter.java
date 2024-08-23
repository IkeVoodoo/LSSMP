package me.ikevoodoo.lssmp.configuration;


import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.lssmp.configuration.data.types.HeartLossMode;
import me.ikevoodoo.lssmp.configuration.data.types.PlayerDropHeartsMode;
import me.ikevoodoo.lssmp.configuration.data.types.TotemUseMode;
import org.bukkit.configuration.file.YamlConfiguration;

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

}
