package me.ikevoodoo.lssmp.config;

import me.ikevoodoo.smpcore.config2.annotations.Config;
import me.ikevoodoo.smpcore.config2.annotations.data.Getter;

@Config(value = "items", hidden = true)
public interface ItemConfig {

    @Getter
    HeartItem getHeartItem();

    @Config("heart")
    interface HeartItem {

        @Getter
        Messages getMessages();

        @Config
        interface Messages {

            @Getter
            default String increment() {
                return "§a+%s §4❤";
            }

            @Getter
            default String maxHearts() {
                return "§cYou have reached the maximum amount of hearts!";
            }
        }

        @Getter
        default boolean claimingHeartHeals() {
            return true;
        }
    }

    @Getter
    ReviveBeacon getReviveBeacon();

    @Config
    interface ReviveBeacon {

        @Getter
        Messages getMessages();

        @Getter
        Options getOptions();

        @Config
        interface Messages {
            @Getter
            default String useMessage() {
                return "§cEnter the name of the player you want to revive";
            }

            @Getter
            default String revivedPlayer() {
                return "§aRevived §e%s";
            }

            @Getter
            default String cancelMessage() {
                return "§cType \"§ccancel§c\" to cancel.";
            }

            @Getter
            default String cancelled() {
                return "§aSuccessfully cancelled";
            }
        }

        @Config
        interface Options {

            @Getter(target = "useMenu")
            default boolean shouldUseMenu() {
                return true;
            }
        }
    }

}
