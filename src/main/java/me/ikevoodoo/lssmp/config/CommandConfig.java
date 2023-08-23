package me.ikevoodoo.lssmp.config;


import me.ikevoodoo.smpcore.config2.annotations.Config;
import me.ikevoodoo.smpcore.config2.annotations.data.Getter;

@Config(value = "commands", hidden = true)
public interface CommandConfig {

    @Getter
    EliminateCommand getEliminateCommand();

    @Config
    interface EliminateCommand {
        @Getter
        default String name() {
            return "lseliminate";
        }

        @Getter
        default String perms() {
            return "lssmp.eliminate";
        }


        @Getter
        EliminateAllCommand getEliminateAllCommand();

        @Config
        interface EliminateAllCommand {

            @Getter
            default String name() {
                return "all";
            }

            @Getter
            default String perms() {
                return "lssmp.eliminate.all";
            }

        }

        @Getter
        Messages getMessages();

        @Config
        interface Messages {

            @Getter
            default String eliminatedAllPlayers() {
                return "§cEliminated all players!";
            }

            @Getter
            default String eliminatedPlayers() {
                return "§cEliminated %s players!";
            }

        }
    }

    @Getter
    GiveCommand getGiveCommand();

    @Config
    interface GiveCommand {
        @Getter
        default String name() {
            return "lsgive";
        }

        @Getter
        default String perms() {
            return "lssmp.give";
        }

    }


    @Getter
    PermSetupCommand getPermSetupCommand();

    @Config
    interface PermSetupCommand {
        @Getter
        default String name() {
            return "lsperms";
        }

        @Getter
        default String perms() {
            return "lssmp.perms";
        }

    }

    @Getter
    HealthCommand getHealthCommand();

    @Config
    interface HealthCommand {
        @Getter
        default String name() {
            return "lshealth";
        }

        @Getter
        default String perms() {
            return "lssmp.health";
        }


        @Getter
        HealthAddCommand getHealthAddCommand();

        @Config
        interface HealthAddCommand {
            @Getter
            default String name() {
                return "add";
            }

            @Getter
            default String perms() {
                return "lssmp.health.add";
            }

        }

        @Getter
        HealthGetCommand getHealthGetCommand();

        @Config
        interface HealthGetCommand {
            @Getter
            default String name() {
                return "get";
            }

            @Getter
            default String perms() {
                return "lssmp.health.get";
            }

        }

        @Getter
        HealthSetCommand getHealthSetCommand();

        @Config
        interface HealthSetCommand {
            @Getter
            default String name() {
                return "set";
            }

            @Getter
            default String perms() {
                return "lssmp.health.set";
            }

        }

        @Getter
        HealthSubCommand getHealthSubCommand();

        @Config
        interface HealthSubCommand {
            @Getter
            default String name() {
                return "sub";
            }

            @Getter
            default String perms() {
                return "lssmp.health.sub";
            }

        }

        @Getter
        Messages getMessages();

        @Config
        interface Messages {
            @Getter
            default String getMessage() {
                return "§3%s §6has §a%s §4❤";
            }

            @Getter
            default String setMessage() {
                return "§3%s §6now has §a%s §4❤ §6(Was §a%s §4❤§6)";
            }

            @Getter
            default String setInWorldMessage() {
                return "§3%s §6now has §a%s §4❤ §6in §3%s §6(Was §a%s §4❤§6)";
            }



            // §3PlayerName §a+%s§4❤ §6(§c%s§4❤ §6-> §a%s§4❤§6)
            @Getter
            default String addMessage() {
                return "§3%s §a+%s§4❤ §6(§c%s§4❤ §6-> §a%s§4❤§6)";
            }


            // §3PlayerName §c-%s§4❤ §6(§a%s§4❤ §6-> §c%s§4❤§6)
            @Getter
            default String subMessage() {
                return "§3%s §c-%s§4❤ §6(§a%s§4❤ §6-> §c%s§4❤§6)";
            }


            @Getter
            default String unknownWorld() {
                return "§cCould not find world §3%s";
            }

        }
    }

    @Getter
    ConfigCommand getConfigCommand();

    @Config
    interface ConfigCommand {
        @Getter
        default String name() {
            return "lsconfig";
        }

        @Getter
        default String perms() {
            return "lssmp.config";
        }

        @Getter
        ConfigSetCommand getConfigSetCommand();

        @Config
        interface ConfigSetCommand {
            @Getter
            default String name() {
                return "set";
            }

            @Getter
            default String perms() {
                return "lssmp.config.set";
            }

        }

        @Getter
        ConfigGetCommand getConfigGetCommand();

        @Config
        interface ConfigGetCommand {
            @Getter
            default String name() {
                return "get";
            }

            @Getter
            default String perms() {
                return "lssmp.config.get";
            }

        }

        @Getter
        Messages getMessages();

        @Config
        interface Messages {

            @Getter
            default String configGetMessage() {
                return "§7[§6%config%§7] §b%path% §fis %value%";
            }

            @Getter
            default String configSetMessage() {
                return "§7[§6%config%§7] §b%path% §fwas set to %value%";
            }


            @Getter
            default String value() {
                return "§a%s";
            }

            @Getter
            default String stringValue() {
                return "§a\"%s§a\"";
            }

        }
    }

    @Getter
    RecipeCommand getRecipeCommand();

    @Config
    interface RecipeCommand {
        @Getter
        default String name() {
            return "lsrecipe";
        }

        @Getter
        default String perms() {
            return "lssmp.recipe";
        }



    }

    @Getter
    ReloadCommand getReloadCommand();

    @Config
    interface ReloadCommand {
        @Getter
        default String name() {
            return "lsreload";
        }

        @Getter
        default String perms() {
            return "lssmp.reload";
        }

        @Getter
        Messages getMessages();

        @Config
        interface Messages {
            @Getter
            default String reload() {
                return "§6Reloaded LSSMP";
            }

        }
    }

    @Getter
    DebugCommand getDebugCommand();

    @Config
    interface DebugCommand {
        @Getter
        default String name() {
            return "lsdebug";
        }

        @Getter
        default String perms() {
            return "lssmp.debug";
        }

        @Getter
        Messages getMessages();

        @Config
        interface Messages {
            @Getter
            default String debug() {
                return "Temp string!";
            }

        }
    }

    @Getter
    ResetCommand getResetCommand();

    @Config
    interface ResetCommand {
        @Getter
        default String name() {
            return "lsreset";
        }

        @Getter
        default String perms() {
            return "lssmp.reset";
        }

        @Getter
        ResetAllCommand getResetAllCommand();

        @Config
        interface ResetAllCommand {
            @Getter
            default String name() {
                return "all";
            }

            @Getter
            default String perms() {
                return "lssmp.reset.all";
            }

        }

        @Getter
        Messages getMessages();

        @Config
        interface Messages {
            @Getter
            default String resetAllPlayers() {
                return "§aReset all players!";
            }

            @Getter
            default String resetPlayers() {
                return "§aReset %s players!";
            }

        }
    }

    @Getter
    ReviveCommand getReviveCommand();

    @Config
    interface ReviveCommand {
        @Getter
        default String name() {
            return "lsrevive";
        }

        @Getter
        default String perms() {
            return "lssmp.revive";
        }

        @Getter
        ReviveAllCommand getReviveAllCommand();

        @Config
        interface ReviveAllCommand {
            @Getter
            default String name() {
                return "all";
            }

            @Getter
            default String perms() {
                return "lssmp.revive.all";
            }

        }

        @Getter
        Messages getMessages();

        @Config
        interface Messages {
            @Getter
            default String revivedAllPlayers() {
                return "§aRevived all players!";
            }

            @Getter
            default String revivedPlayers() {
                return "§aRevived %s players!";
            }

        }
    }

    @Getter
    UpgradeCommand getUpgradeCommand();

    @Config
    interface UpgradeCommand {
        @Getter
        default String name() {
            return "lsupgrade";
        }

        @Getter
        default String perms() {
            return "lssmp.upgrade";
        }

        @Getter
        Messages getMessages();

        @Config
        interface Messages {
            @Getter
            default String warning() {
                return "§c§lWARNING!§r§c Using this command will reset your configs and restart the server, type \"/lsupgrade confirm\" if you are sure.";
            }

            @Getter
            default String noUpgradeNeeded() {
                return "&aNo upgrade needed! Type \"/lsupgrade force\" to force an upgrade!";
            }

        }
    }

    @Getter
    WithdrawCommand getWithdrawCommand();

    @Config
    interface WithdrawCommand {
        @Getter
        default String name() {
            return "lswithdraw";
        }

        @Getter
        default String perms() {
            return "lssmp.withdraw";
        }

        @Getter
        Messages getMessages();

        @Config
        interface Messages {
            @Getter
            default String withdraw() {
                return "§6You have withdrawn %amount% heart(s)!";
            }

            @Getter
            default String withdrawnTooMuch() {
                return "§cWithdraw count is too high! Lower it to at least %max%!";
            }

            @Getter
            default String withdrawnTooLittle() {
                return "§cWithdraw count is too little! Withdraw at least one heart!";
            }

            @Getter
            default String notEnoughSpace() {
                return "§cYou do not have enough space to fit %amount% heart(s) in your inventory!";
            }

        }

        @Getter
        default boolean withdrawEliminates() {
            return false;
        }

    }

}
