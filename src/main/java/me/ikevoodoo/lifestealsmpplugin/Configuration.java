package me.ikevoodoo.lifestealsmpplugin;

import org.bukkit.attribute.AttributeInstance;
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
    private static String killerNotOnline, killerDisconnected;

    private static final HashMap<String, Object> configKeys = new HashMap<>();

    private static final List<UUID> eliminated = new ArrayList<>();

    static {
        configKeys.put("elimination.shouldEliminate", true);

        configKeys.put("elimination.bans.shouldBan", false);
        configKeys.put("elimination.bans.banMessage", "&cYou been banned due to loosing all of your hearts, your last killer was &4%player%");
        configKeys.put("elimination.bans.broadcastBan", false);
        configKeys.put("elimination.bans.broadcastMessage", "&c%player% has lost all of it's hearts and has been banned.");

        configKeys.put("elimination.spectate.shouldSpectate", true);
        configKeys.put("elimination.spectate.killerNotOnline", "&cYour killer is not online so you are not allowed to spectate!");
        configKeys.put("elimination.spectate.killerDisconnected", "&cYour killer has disconnected!");
    }

    public static void init() {
        plugin = LifestealSmpPlugin.getInstance();
        setup();
        reload();
    }

    // IntelliJ NPE warnings about config
    @SuppressWarnings("all")
    public static void reload() {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        shouldEliminate = configuration.getBoolean("elimination.shouldEliminate");

        shouldBan = configuration.getBoolean("elimination.bans.shouldBan");
        broadcastBan = configuration.getBoolean("elimination.bans.broadcastBan");
        broadcastMessage = configuration.getString("elimination.bans.broadcastMessage");
        banMessage = configuration.getString("elimination.bans.banMessage");

        shouldSpectate = configuration.getBoolean("elimination.spectate.shouldSpectate");
        killerNotOnline = configuration.getString("elimination.spectate.killerNotOnline");
        killerDisconnected = configuration.getString("elimination.spectate.killerDisconnected");

        List<UUID> tempEliminated = new ArrayList<>();
        if(configuration.contains("eliminated")) {
            for (String s : configuration.getConfigurationSection("eliminated").getKeys(false)) {
                if(s.equals("console")) {
                    for(String s1 : configuration.getConfigurationSection("eliminated.console").getKeys(false)) {
                        tempEliminated.add(UUID.fromString(s1));
                    }
                } else if (!s.equals("killers"))
                    tempEliminated.add(UUID.fromString(s));
            }
        }

        eliminated.clear();
        eliminated.addAll(tempEliminated);
    }

    private static void save() {
        try {
            plugin.getConfig().save(plugin.getDataFolder() + "/config.yml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setup() {
        configuration = plugin.getConfig();
        configKeys.forEach((key, def) -> {
            if(!configuration.contains(key))
                configuration.set(key, def);
        });
        save();
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

    public static String getKillerNotOnline() {
        return killerNotOnline;
    }

    public static String getKillerDisconnected() {
        return killerDisconnected;
    }

    public static void addElimination(Player player, UUID killerId) {
        if(killerId != null) {
            configuration.set("eliminated." + player.getUniqueId(), killerId.toString());
            configuration.set("eliminated.killers." + killerId, player.getUniqueId());
        } else
            configuration.set("eliminated.console." + player.getUniqueId(), "console");
        save();
        eliminated.add(player.getUniqueId());
        AttributeInstance maxHp = Utils.getMaxHealth(player);
        maxHp.setBaseValue(20D);
    }

    public static void banID(UUID id, String reason) {
        configuration.set("bans." + id, reason);
        save();
    }

    public static void unbanID(UUID id) {
        configuration.set("bans." + id, null);
        save();
    }

    public static boolean isBanned(UUID id) {
        return configuration.contains("bans." + id);
    }

    public static String getBanMessage(UUID id) {
        return configuration.getString("bans." + id);
    }

    public static void revive(UUID id) {
        String uuid = configuration.getString("eliminated." + id);
        if(uuid != null)
            configuration.set("eliminated.killers." + uuid, null);
        configuration.set("eliminated.console." + id, null);
        configuration.set("eliminated." + id, null);
        eliminated.remove(id);
        unbanID(id);
        save();
    }

    public static void reviveOnlyDead(UUID id) {
        configuration.set("eliminated.console." + id, null);
        configuration.set("eliminated." + id, null);
    }

    public static boolean isEliminated(Player player) {
        return isEliminated(player.getUniqueId());
    }

    public static String getKiller(UUID id) {
        return configuration.getString("eliminated." + id);
    }

    public static String getKilled(UUID id) {
        return configuration.getString("eliminated.killers." + id);
    }

    public static boolean isEliminated(UUID uuid) {
        return eliminated.contains(uuid);
    }

    public static List<UUID> getEliminations() {
        List<UUID> list = new ArrayList<>();
        for(UUID id : eliminated)
            list.add(UUID.fromString(id.toString()));
        return list;
    }

}
