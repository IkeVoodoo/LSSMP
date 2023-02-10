package me.ikevoodoo.lssmp.config.bans;

import me.ikevoodoo.lssmp.config.errors.ConfigError;
import me.ikevoodoo.lssmp.config.errors.Some;
import me.ikevoodoo.smpcore.utils.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

public record BanTimeData(
        String banMessage,

        boolean broadcastBan,
        String broadcastBanMessage,

        String permission,
        long time
) {

    public static Some<BanTimeData> fromConfig(ConfigurationSection section) {
        if (section == null)
            return Some.error(new ConfigError(null, "Section is null"));

        var banMessage = section.getString("ban-message");
        if (banMessage == null)
            return Some.error(new ConfigError(section.getName(), "Ban message is null"));

        var broadcastBan = section.getBoolean("broadcast-ban");
        var broadcastBanMessage = section.getString("broadcast-ban-message");
        if (broadcastBan && broadcastBanMessage == null)
            return Some.error(new ConfigError(section.getName(), "Broadcast ban message is enabled but message is null!"));

        var permission = section.getString("permission");
        if (permission == null)
            return Some.error(new ConfigError(section.getName(), "Permission is null"));

        var isPermanent = section.getBoolean("permanent");
        long banTime = Long.MAX_VALUE;
        if (!isPermanent) {
            var time = section.getString("time");
            if (time == null)
                return Some.error(new ConfigError(section.getName(), "Time is null"));


            var parsedTime = parseBanTime(time);
            if (parsedTime.isError())
                return Some.error(parsedTime.error().addKey(section.getName()));

            banTime = parsedTime.key();
        }


        return Some.of(new BanTimeData(
                StringUtils.color(banMessage),
                broadcastBan,
                StringUtils.color(broadcastBanMessage),
                permission,
                banTime
        ));
    }

    private static Some<Long> parseBanTime(String time) {
        var split = time.split(":");
        if (split.length != 3)
            return Some.error(new ConfigError(null, "Time is not in the format hh:mm:ss"));

        var splitTime = new String[4];
        splitTime[0] = split[0];
        splitTime[1] = split[1];
        splitTime[3] = "0";

        if (split[2].contains(".")) {
            var split2 = split[2].split("\\.");
            splitTime[2] = split2[0];
            splitTime[3] = split2[1];
        } else {
            splitTime[2] = split[2];
        }

        var hours = parseLong(splitTime[0]);
        if (hours.isError())
            return Some.error(hours.error().addKey("hours"));

        var minutes = parseLong(splitTime[1]);
        if (minutes.isError())
            return Some.error(minutes.error().addKey("minutes"));

        var seconds = parseLong(splitTime[2]);
        if (seconds.isError())
            return Some.error(seconds.error().addKey("seconds"));

        var millis = parseLong(splitTime[3]);
        if (millis.isError())
            return Some.error(millis.error().addKey("millis"));

        return Some.of(hours.key() * 3600000L + minutes.key() * 60000L + seconds.key() * 1000L + millis.key());
    }

    private static Some<Long> parseLong(String str) {
        try {
            return Some.of(Long.parseLong(str));
        } catch (NumberFormatException e) {
            return Some.error(new ConfigError(null, "Could not parse number"));
        }
    }

}
