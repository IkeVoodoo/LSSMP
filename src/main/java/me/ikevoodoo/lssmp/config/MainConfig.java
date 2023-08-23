package me.ikevoodoo.lssmp.config;

import me.ikevoodoo.lssmp.LSSMP;
import me.ikevoodoo.smpcore.config.annotations.ListType;
import me.ikevoodoo.smpcore.config2.annotations.Config;
import me.ikevoodoo.smpcore.config2.annotations.data.CollectionType;
import me.ikevoodoo.smpcore.config2.annotations.data.Getter;
import me.ikevoodoo.smpcore.utils.StringUtils;
import org.bukkit.World;

import java.util.List;

@Config(value = "config", hidden = true)
public interface MainConfig {

    @Getter
    default boolean autoConfigReload() {
        return false;
    }

    @Getter(target = "elimination")
    Elimination getEliminationConfig();

    @Config
    interface Elimination {

        @Getter
        default boolean environmentStealsHearts() {
            return true;
        }

        @Getter
        default double environmentHealthScale() {
            return 1.0;
        }

        @Getter
        default double healthScale() {
            return 1.0;
        }

        @Getter
        default double maxHearts() {
            return 20.0;
        }

        @Getter
        default boolean useMaxHealth() {
            return true;
        }

        @Getter
        default double minHearts() {
            return 5.0;
        }

        @Getter
        default boolean useMinHealth() {
            return false;
        }

        @Getter
        default boolean banAtMinHealth() {
            return true;
        }

        @Getter
        default double defaultHearts() {
            return 10.0;
        }

        @Getter
        default boolean useReviveHearts() {
            return true;
        }

        @Getter
        default double reviveHearts() {
            return 10.0;
        }

        @Getter
        default boolean totemWorksInInventory() {
            return false;
        }

        @Getter
        default boolean alwaysDropHearts() {
            return false;
        }

        @Getter
        default boolean environmentDropHearts() {
            return false;
        }

        @Getter
        default boolean playersDropHearts() {
            return false;
        }

        @Getter
        default boolean fullInventoryHeartDrop() {
            return true;
        }

        @Getter
        default boolean allowSelfElimination() {
            return false;
        }

        @Getter(target = "bans")
        Bans getBansConfig();

        @Config
        interface Bans {

            @Getter
            default String banMessage() {
                return "§cYou have been eliminated!";
            }

            @Getter
            default boolean useBanTime() {
                return false;
            }

            @Getter
            default String banTime() {
                return "00:00:00.0000";
            }

            @Getter
            default boolean broadcastBan() {
                return false;
            }

            @Getter
            default String broadcastMessage() {
                return "§c%player% has lost all of their hearts and has been banned.";
            }

            default long getBanTime() {
                return useBanTime() ? StringUtils.parseBanTime(banTime()) : Long.MAX_VALUE;
            }
        }

        @Getter
        default boolean perWorldHearts() {
            return false;
        }

        @ListType(String.class)
        default List<String> allowedWorlds() {
            return List.of("all");
        }

        default boolean isWorldAllowed(World world) {
            return allowedWorlds().contains("all") || allowedWorlds().contains(world.getName());
        }

        default double getMax() {
            return (useMaxHealth() ? maxHearts() : 1024) * 2;
        }

        default double getMinHealth() {
            return (useMinHealth() ? minHearts() : 0) * 2;
        }

        default double getMinHearts() {
            return getMinHealth() * 2;
        }

        default double getHeartScale() {
            return healthScale() * 2;
        }

        default double getEnvironmentHeartScale() {
            return environmentHealthScale() * 2;
        }
    }


    @Getter(target = "items")
    Items getItemsConfig();

    @Config
    interface Items {

        @Getter
        Heart getHeart();

        @Config
        interface Heart {

            @Getter
            default String displayName() {
                return "§c❤ §fExtra heart.";
            }

            @CollectionType(String.class)
            default List<String> lore() {
                return List.of("Gives you an extra heart!");
            }

            @Getter
            default int customModelData() {
                return 931;
            }
        }


        @Getter
        Beacon getBeacon();

        @Config
        interface Beacon {

            @Getter
            default String displayName() {
                return "§fRevive Beacon.";
            }

            @CollectionType(String.class)
            default List<String> lore() {
                return List.of("Right click to revive!");
            }

            @Getter
            default int customModelData() {
                return 932;
            }
        }


        @Getter
        HeartFragment getHeartFragment();

        @Config
        interface HeartFragment {

            @Getter
            default String displayName() {
                return "§c§lHeart Fragment.";
            }

            @CollectionType(String.class)
            default List<String> lore() {
                return List.of("Use 4 of these to craft a heart!");
            }

            @Getter
            default int customModelData() {
                return 933;
            }
        }
    }


    @Getter(target = "messages")
    Messages getMessages();

    @Config
    interface Messages {

        @Getter(target = "errors")
        Errors getErrorMessages();

        @Config
        interface Errors {

            @Getter
            default String requiresPlayer() {
                return "§cA player is required to perform this command!";
            }

            @Getter
            default String requiresArgument() {
                return "§cThe argument \"%s\" is required to perform this command!";
            }

            @Getter
            default String specifyAtLeastOne() {
                return "§cYou must specify at least one %s!";
            }

            @Getter
            default String notFound() {
                return "§c%s not found!";
            }
        }
    }

    @Getter
    default int doNotTouch_configVersion() {
        return LSSMP.CURRENT_CONFIG_VERSION;
    }

}
