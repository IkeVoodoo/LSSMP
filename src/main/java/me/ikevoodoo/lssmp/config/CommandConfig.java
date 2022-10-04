package me.ikevoodoo.lssmp.config;

import me.ikevoodoo.smpcore.config.annotations.Config;

@Config("commands.yml")
public class CommandConfig {

    public static class EliminateCommand {
        public static String name = "lseliminate";
        public static String perms = "lssmp.eliminate";

        public static class EliminateAllCommand {

            public static String name = "all";
            public static String perms = "lssmp.eliminate.all";
        }

        public static class Messages {

            public static String eliminatedAllPlayers = "§cEliminated all players!";
            public static String eliminatedPlayers = "§cEliminated %s players!";
        }
    }

    public static class GiveCommand {
        public static String name = "lsgive";
        public static String perms = "lssmp.give";
    }

    public static class HealthCommand {
        public static String name = "lshealth";
        public static String perms = "lssmp.health";

        public static class HealthAddCommand {
            public static String name = "add";
            public static String perms = "lssmp.health.add";
        }

        public static class HealthGetCommand {
            public static String name = "get";
            public static String perms = "lssmp.health.get";
        }

        public static class HealthSetCommand {
            public static String name = "set";
            public static String perms = "lssmp.health.set";
        }

        public static class HealthSubCommand {
            public static String name = "sub";
            public static String perms = "lssmp.health.sub";
        }

        public static class Messages {
            public static String getMessage = "§3%s §6 has §a%s §4❤";
        }
    }

    public static class RecipeCommand {
        public static String name = "lsrecipe";
        public static String perms = "lssmp.recipe";


    }

    public static class ReloadCommand {
        public static String name = "lsreload";
        public static String perms = "lssmp.reload";

        public static class Messages {
            public static String reload = "§6Reloaded LSSMP";
        }
    }

    public static class ResetCommand {
        public static String name = "lsreset";
        public static String perms = "lssmp.reset";

        public static class ResetAllCommand {
            public static String name = "all";
            public static String perms = "lssmp.reset.all";
        }

        public static class Messages {
            public static String resetAllPlayers = "§aReset all players!";
            public static String resetPlayers = "§aReset %s players!";
        }
    }

    public static class ReviveCommand {
        public static String name = "lsrevive";
        public static String perms = "lssmp.revive";

        public static class ReviveAllCommand {
            public static String name = "all";
            public static String perms = "lssmp.revive.all";
        }

        public static class Messages {
            public static String revivedAllPlayers = "§aRevived all players!";
            public static String revivedPlayers = "§aRevived %s players!";
        }
    }

    public static class UpgradeCommand {
        public static String name = "lsupgrade";
        public static String perms = "lssmp.upgrade";

        public static class Messages {
            public static String warning = "§c§lWARNING!§r§c Using this command will reset your configs and restart the server, type \"/lsupgrade confirm\" if you are sure.";
            public static String noUpgradeNeeded = "&aNo upgrade needed! Type \"/lsupgrade force\" to force an upgrade!";
        }
    }

    public static class WithdrawCommand {
        public static String name = "lswithdraw";
        public static String perms = "lssmp.withdraw";

        public static class Messages {
            public static String withdraw = "§6You have withdrawn %amount% heart(s)!";
            public static String withdrawnTooMuch = "§cWithdraw count is too high! Lower it to at least %max%!";
            public static String notEnoughSpace = "§cYou do not have enough space to fit %amount% heart(s) in your inventory!";
        }
    }

}
