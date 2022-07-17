package me.ikevoodoo.lssmp.config;

import me.ikevoodoo.smpcore.config.annotations.Config;

@Config("items.yml")
public class ItemConfig {

    public static class HeartItem {
        public static class Messages {
            public static String increment = "§a+%s §4❤";
            public static String maxHearts = "§cYou have reached the maximum amount of hearts!";
        }

        public static boolean claimingHeartHeals = true;
    }

    public static class ReviveBeacon {
        public static class Messages {
            public static String useMessage = "§cEnter the name of the player you want to revive";
            public static String revivedPlayer = "§aRevived §e%s";
            public static String cancelMessage = "§cType \"§ccancel§c\" to cancel.";
            public static String cancelled = "§aSuccessfully cancelled";
        }
    }

}
