package me.ikevoodoo.lssmp.config;

import me.ikevoodoo.lssmp.LSSMP;
import me.ikevoodoo.smpcore.config.annotations.Config;
import me.ikevoodoo.smpcore.config.annotations.ListType;
import me.ikevoodoo.smpcore.utils.StringUtils;
import org.bukkit.World;

import java.util.List;

@Config("config.yml")
public class MainConfig {

    public static boolean autoConfigReload = false;

    public static class Elimination {
        public static boolean environmentStealsHearts = true;
        public static double environmentHealthScale = 1.0;
        public static double healthScale = 1.0;
        public static double maxHearts = 20.0;
        public static boolean useMaxHealth = true;

        public static double minHearts = 5.0;
        public static boolean useMinHealth = false;
        public static boolean banAtMinHealth = true;
        public static double defaultHearts = 10.0;

        public static boolean useReviveHearts = true;
        public static double reviveHearts = 10.0;

        public static boolean totemWorksInInventory = false;

        public static boolean alwaysDropHearts = false;
        public static boolean environmentDropHearts = false;
        public static boolean playersDropHearts = false;

        public static boolean fullInventoryHeartDrop = true;

        public static boolean allowSelfElimination = false;

        public static class Bans {
            public static String banMessage = "§cYou have been eliminated!";

            public static boolean useBanTime = false;
            public static String banTime = "00:00:00.0000";

            public static boolean broadcastBan = false;
            public static String broadcastMessage = "§c%player% has lost all of their hearts and has been banned.";

            public static long getBanTime() {
                return useBanTime ? StringUtils.parseBanTime(banTime) : Long.MAX_VALUE;
            }
        }

        public static boolean perWorldHearts = false;

        @ListType("java.lang.String")
        public static List<String> allowedWorlds = List.of("all");

        public static boolean isWorldAllowed(World world) {
            return allowedWorlds.contains("all") || allowedWorlds.contains(world.getName());
        }

        public static double getMax() {
            return (useMaxHealth ? maxHearts : 1024) * 2;
        }

        public static double getMin() {
            return (useMinHealth ? minHearts : 0) * 2;
        }

        public static double getMinHearts() {
            return getMin() * 2;
        }

        public static double getHeartScale() {
            return healthScale * 2;
        }

        public static double getEnvironmentHeartScale() {
            return environmentHealthScale * 2;
        }
    }

    public static class Items {
        public static class Heart {
            public static String displayName = "§c❤ §fExtra heart.";

            @ListType("java.lang.String")
            public static List<String> lore = List.of("Gives you an extra heart!");
            public static int customModelData = 931;
        }

        public static class Beacon {
            public static String displayName = "§fRevive Beacon.";

            @ListType("java.lang.String")
            public static List<String> lore = List.of("Right click to revive!");
            public static int customModelData = 932;
        }

        public static class HeartFragment {
            public static String displayName = "§c§lHeart Fragment.";

            @ListType("java.lang.String")
            public static List<String> lore = List.of("Use 4 of these to craft a heart!");
            public static int customModelData = 933;
        }
    }

    public static class Messages {
        public static class Errors {
            public static String requiresPlayer = "§cA player is required to perform this command!";
            public static String requiresArgument = "§cThe argument \"%s\" is required to perform this command!";
            public static String specifyAtLeastOne = "§cYou must specify at least one %s!";
            public static String notFound = "§c%s not found!";
        }
    }

    public static int doNotTouch_configVersion = LSSMP.CURRENT_CONFIG_VERSION;

}
