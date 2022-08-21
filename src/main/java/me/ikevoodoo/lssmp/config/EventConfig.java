package me.ikevoodoo.lssmp.config;

import me.ikevoodoo.smpcore.config.annotations.Config;

@Config("events.yml")
public class EventConfig {

    public static class Events {
        public static class Eliminated {
            public static class Example {
                public static class Execute {
                    public static String command = "put your command here";
                    public static String as = "console";
                }
            }
        }

        public static class Revived {
            public static class Example {
                public static class Execute {
                    public static String command = "put your command here";
                    public static String as = "console";
                }
            }
        }
    }

}
