package me.ikevoodoo.lssmp.config;

import me.ikevoodoo.smpcore.config.annotations.Config;
import org.bukkit.World;

import java.util.List;

@Config("config.yml")
public class ConfigFile {

    public static class Elimination {
        public static boolean environmentStealsHearts = true;
        public static double environmentHealthScale = 1.0;
        public static double healthScale = 1.0;
        public static double maxHearts = 20.0;
        public static boolean useMaxHealth = true;
        public static double defaultHearts = 10.0;

        public static class Bans {
            public static boolean useBanTime = false;
            public static String banTime = "00:00:00.0000";

            public static boolean broadcastBan = false;
            public static String broadcastMessage = "&c%player% has lost all of their hearts and has been banned.";
        }

        public static class Items {
            public static class Heart {
                public static String displayName = "§c❤ §fExtra heart.";
            }
        }

        public static List<String> allowedWorlds = List.of("all");

        public static boolean isWorldAllowed(World world) {
            return allowedWorlds.contains("all") || allowedWorlds.contains(world.getName());
        }

        public static double getMax() {
            return (useMaxHealth ? maxHearts : 1024) * 2;
        }
    }

}
