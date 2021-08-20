package me.ikevoodoo.lifestealsmpplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;
import java.util.UUID;

public class Utils {

    public static double parseHearts(double hp) {
        return hp + Double.parseDouble(String.valueOf(hp).replaceFirst("\\.0+$", ""));
    }

    public static AttributeInstance getMaxHealth(Player player) {
        return player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    }

    public static void modifyHealth(Player player, double scale) {
        AttributeInstance maxHp = getMaxHealth(player);


        // Fixing crashes!
        if(maxHp.getBaseValue() + scale < 0)
            maxHp.setBaseValue(0);
        else maxHp.setBaseValue(maxHp.getValue() + scale);

        // Please, Please work
        if(Configuration.shouldScaleHealth() && player.getHealth() + scale > 0) player.setHealth(player.getHealth() + scale);
    }

    public static boolean shouldEliminate(Player player) {
        return getMaxHealth(player).getValue() == 0;
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

            // Use this rather than the player#kick or player#kickPlayer to support multiple server software
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
                    String.format("kick %s %s", player.getName(), Configuration.getBanMessage().replace("%player%", killerName))
            );
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
