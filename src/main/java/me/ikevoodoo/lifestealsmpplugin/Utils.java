package me.ikevoodoo.lifestealsmpplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Utils {



    public static AttributeInstance getMaxHealth(Player player) {
        return player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    }

    public static void modifyHealth(Player player, int scale) {
        AttributeInstance maxHp = getMaxHealth(player);
        maxHp.setBaseValue(maxHp.getValue() + scale);
        if(Configuration.shouldScaleHealth()) player.setHealth(player.getHealth() + scale);
    }

    public static boolean shouldEliminate(Player player) {
        return getMaxHealth(player).getValue() == 2;
    }

    public static void eliminate(Player player, Player killer) {
        if(!Configuration.shouldEliminate()) {
            return;
        }

        String killerName = killer != null ? killer.getName() : "Environment";
        UUID id = killer != null ? killer.getUniqueId() : player.getWorld().getUID();

        if(Configuration.shouldBan()) {
            Configuration.addElimination(player, id);
            if(Configuration.shouldBroadcastBan())
                Bukkit.broadcastMessage(getFromText(Configuration.getBroadcastMessage().replace("%player%", player.getName())));
            Configuration.banID(player.getUniqueId(), Configuration.getBanMessage().replace("%player%", killerName));
            player.kickPlayer(getFromText(Configuration.getBanMessage().replace("%player%", killerName)));
        } else if (Configuration.shouldSpectate()) {
            player.setGameMode(GameMode.SPECTATOR);
            player.setSpectatorTarget(killer);
            Configuration.addElimination(player, id);
        }
    }

    public static String getFromText(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }


}
