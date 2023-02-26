package me.ikevoodoo.lssmp.config;

import me.ikevoodoo.smpcore.config.annotations.Config;
import me.ikevoodoo.smpcore.config.annotations.ListType;

import java.util.List;

@Config("menus.yml")
public class MenuConfig {

    public static class ReviveBeacon {
        public static class Head {
            public static String headName = "<<green>><<bold>>Revive <<red>>%player name%";

            @ListType("java.lang.String")
            public static List<String> lore = List.of("", "<<green>><<bold>>Banned for <<red>>%ban time%");

            public static class Messages {
                public static String notEliminated = "[[bold]]<<red>>The player <<green>>%player name% <<red>>is not eliminated.";
                public static String revived = "[[bold]]<<green>>Revived <<red>>%player name%";
            }
        }
    }

}
