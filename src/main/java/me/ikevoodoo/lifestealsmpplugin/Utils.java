package me.ikevoodoo.lifestealsmpplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class Utils {

    public static AttributeInstance getMaxHealth(Player player) {
        return player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    }

    public static void modifyHealth(Player player, int scale) {
        AttributeInstance maxHp = getMaxHealth(player);
        maxHp.setBaseValue(maxHp.getValue() + scale);
    }

    public static boolean shouldEliminate(Player player) {
        return getMaxHealth(player).getValue() - 2 == 0;
    }

    @SuppressWarnings("deprecation")
    public static void eliminate(Player player, Player killer) {
        if(!Configuration.shouldEliminate()) return;

        if(Configuration.shouldBan()) {
            Configuration.addElimination(player);
            if(Configuration.shouldBroadcastBan()) {
                Bukkit.broadcastMessage(
                        ChatColor.translateAlternateColorCodes('&',
                                Configuration.getBroadcastMessage().replace("%player%", player.getName())
                        )
                );
            }

            player.banPlayer(ChatColor.translateAlternateColorCodes('&',
                    Configuration.getBanMessage().replace("%player%", killer.getName())
            )).save();
        } else if (Configuration.shouldSpectate()) {
                Configuration.addElimination(player);
                player.setGameMode(GameMode.SPECTATOR);
                player.setSpectatorTarget(killer);
        }
    }


}
