package me.ikevoodoo.lifestealsmpplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class Configuration {

    private static LifestealSmpPlugin plugin;
    private static FileConfiguration configuration;

    private static boolean shouldEliminate;

    private static boolean shouldBan, broadcastBan;
    private static String banMessage, broadcastMessage;

    private static boolean shouldSpectate;

    private static final HashMap<String, Object> configKeys = new HashMap<>();

    private static final List<UUID> eliminated = new ArrayList<>();

    static {
        configKeys.put("elimination.shouldEliminate", true);

        configKeys.put("elimination.bans.shouldBan", false);
        configKeys.put("elimination.bans.banMessage", "&cYou been banned due to loosing all of your hearts, your last killer was &4%player%");
        configKeys.put("elimination.bans.broadcastBan", false);
        configKeys.put("elimination.bans.broadcastMessage", "&c%player% has lost all of it's hearts and has been banned.");

        configKeys.put("elimination.spectate.shouldSpectate", true);
    }

    public static void init() {
        plugin = LifestealSmpPlugin.getInstance();
        setup();
        reload();
    }

    // IntelliJ NPE warnings about config
    @SuppressWarnings("all")
    public static void reload() {
        shouldEliminate = configuration.getBoolean("elimination.bans.shouldEliminate");

        shouldBan = configuration.getBoolean("elimination.bans.shouldBan");
        broadcastBan = configuration.getBoolean("elimination.bans.broadcastBan");
        broadcastMessage = configuration.getString("elimination.bans.broadcastMessage");
        banMessage = configuration.getString("elimination.bans.banMessage");

        shouldSpectate = configuration.getBoolean("elimination.spectate.shouldSpectate");

        List<UUID> tempEliminated = new ArrayList<>();
        if(configuration.contains("eliminated"))
            for(String s : configuration.getConfigurationSection("eliminated").getKeys(false))
                tempEliminated.add(UUID.fromString(configuration.getString(s)));

        eliminated.clear();
        eliminated.addAll(tempEliminated);
    }

    private static void setup() {
        configuration = plugin.getConfig();
        configKeys.forEach((key, def) -> {
            if(!configuration.contains(key))
                configuration.set(key, def);
        });
    }

    public static boolean shouldEliminate() {
        return shouldEliminate;
    }

    public static boolean shouldBan() {
        return shouldBan;
    }

    public static boolean shouldBroadcastBan() {
        return broadcastBan;
    }

    public static String getBroadcastMessage() {
        return broadcastMessage;
    }

    public static String getBanMessage() {
        return banMessage;
    }

    public static boolean shouldSpectate() {
        return shouldSpectate;
    }

    public static void addElimination(Player player) {
        configuration.set("eliminated." + player.getUniqueId(), player.getUniqueId().toString());
        eliminated.add(player.getUniqueId());
    }

    public static void revive(UUID id) {
        configuration.set("eliminated." + id, null);
        eliminated.remove(id);
    }

    public static boolean isEliminated(Player player) {
        return isEliminated(player.getUniqueId());
    }

    public static boolean isEliminated(UUID uuid) {
        return eliminated.contains(uuid);
    }

    public static List<UUID> getEliminations() {
        return eliminated;
    }

}
