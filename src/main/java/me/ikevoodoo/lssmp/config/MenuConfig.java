package me.ikevoodoo.lssmp.config;

import me.ikevoodoo.smpcore.config2.annotations.Config;
import me.ikevoodoo.smpcore.config2.annotations.data.Getter;

import java.util.List;

@Config
public interface MenuConfig {

    @Getter
    ReviveBeacon getReviveBeacon();

    interface ReviveBeacon {
        interface Head {
            @Getter
            default String headName() {
                return "<<green>><<bold>>Revive <<red>>%player name%";
            }

            @Getter
            default List<String> lore() {
                return List.of("", "<<green>><<bold>>Banned for <<red>>%ban time%");
            }


            interface Messages {
                @Getter
                default String notEliminated() {
                    return "[[bold]]<<red>>The player <<green>>%player name% <<red>>is not eliminated.";
                }

                @Getter
                default String revived() {
                    return "[[bold]]<<green>>Revived <<red>>%player name%";
                }

            }
        }
    }

}
