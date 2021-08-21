package me.ikevoodoo.lifestealsmpplugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ikevoodoo.lifestealsmpplugin.bstats.Metrics;
import me.ikevoodoo.lifestealsmpplugin.commands.*;
import me.ikevoodoo.lifestealsmpplugin.commands.suggestions.HealthCommandTabCompleter;
import me.ikevoodoo.lifestealsmpplugin.events.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LifestealSmpPlugin extends JavaPlugin {

    public static NamespacedKey heartKey;
    public static final ItemStack heartItem = new ItemStack(Material.RED_DYE);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static LifestealSmpPlugin instance;
    private static Metrics metrics;

    private static ScheduledFuture<?> updateTask;

    public static final Logger LOGGER = Logger.getLogger("LSSMP");

    public static Recipe currentHeartRecipe;
    public static File heartRecipeFile;

    public static final String prefix = ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "LSSMP" + ChatColor.DARK_AQUA + "] " + ChatColor.RESET;

    public static boolean updateAvailable = false, updating = false;

    // IntelliJ NPE warnings for getCommand
    @SuppressWarnings("all")
    @Override
    public void onEnable() {
        instance = this;
        heartKey = new NamespacedKey(this, "lssmp_heart_item");
        ItemMeta itemMeta = heartItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "❤ " + ChatColor.WHITE + "Extra heart.");
        itemMeta.setLore(Arrays.asList("Gives you an extra heart!"));
        heartItem.setItemMeta(itemMeta);
        saveConfig();
        Configuration.init();
        File recipesFolder = new File(getDataFolder().getPath() + File.separator + "recipes");
        if(!recipesFolder.exists()) {
            if(!recipesFolder.mkdir()) {
                throw new IllegalStateException("Could not create essential folder: " + recipesFolder.getPath() + " | Please create the folder yourself and restart the server.");
            }
        }

        heartRecipeFile = new File(recipesFolder + File.separator + "heartRecipe.json");
        if(!heartRecipeFile.exists()) {
            try {
                heartRecipeFile.createNewFile();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not create file: " + heartRecipeFile.getPath() + " | The plugin might break.");
            }
        }

        try(PrintWriter pw = new PrintWriter(new FileWriter(heartRecipeFile))) {
            String text = """
                    {
                        "enabled": true,
                        "shaped": true,
                        "outputAmount": 1,
                        "recipe": [
                            "ODO",
                            "D D",
                            "ODO"
                        ],
                        "items": {
                            "O": {
                                "item": "minecraft:obsidian"
                            },
                            "D": {
                                "item": "minecraft:diamond_block"
                            }
                        }
                    }
                    """;
            pw.print(text);
            currentHeartRecipe = loadRecipe(text);
            Bukkit.addRecipe(currentHeartRecipe);
        } catch (Exception e) {
            e.printStackTrace();
        }

        metrics = new Metrics(this, 12177);

        checkForUpdates();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for(UUID id : Configuration.getEliminations()) {
                Player player = Bukkit.getPlayer(id);
                if(player == null) continue;
                if(player.getSpectatorTarget() == null) {
                    player.setSpectatorTarget(Bukkit.getPlayer(UUID.fromString(Configuration.getKiller(id))));
                }
            }
        }, 0, 10);

        updateTask = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            checkForUpdates();
        }, 0, 5, TimeUnit.SECONDS);

        updateMetrics();
        
        getCommand("lsreload").setExecutor(new ReloadCommand());
        getCommand("lseliminate").setExecutor(new EliminateCommand());
        getCommand("lsrevive").setExecutor(new ReviveCommand());
        //getCommand("lsupdate").setExecutor(new UpdateCommand());
        getCommand("lsversion").setExecutor(new VersionCommand());

        getCommand("lshealth").setExecutor(new HealthCommand());
        getCommand("lshealth").setTabCompleter(new HealthCommandTabCompleter());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        if(updateTask != null) {
            updateTask.cancel(true);
            updateTask = null;
        }
    }

    public static LifestealSmpPlugin getInstance() { return instance; }

    public static void updateMetrics() {
        metrics.addCustomChart(new Metrics.AdvancedPie("eliminations", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put(Configuration.shouldEliminate()         ? "Eliminates"                : "Does not eliminate",                1);
            valueMap.put(Configuration.environmentStealsHearts() ? "Environment steals hearts" : "Environment does not steal hearts", 1);
            valueMap.put(Configuration.shouldScaleHealth()       ? "Scales Health"             : "Does not scale health",             1);
            valueMap.put(Configuration.shouldBan()               ? "Bans"                      : "Does not ban",                      1);
            valueMap.put(Configuration.shouldBroadcastBan()      ? "Broadcasts ban"            : "Does not broadcast ban",            1);
            valueMap.put(Configuration.shouldSpectate()          ? "Has spectators"            : "Does not have spectators",          1);
            return valueMap;
        }));
    }

    public static Recipe loadRecipe(File file) throws IOException {
        return loadRecipe(String.join("\n", Files.readAllLines(file.toPath())));
    }

    public static Recipe loadRecipe(String text) throws IOException {
        JsonNode json = mapper.readTree(text);
        JsonNode enabled = json.get("enabled");
        boolean isEnabled = enabled != null && enabled.asBoolean();
        if(isEnabled) {
            JsonNode outputAmount = json.get("outputAmount");
            int amount = 1;
            if(outputAmount != null) {
                try {
                    amount = Integer.parseInt(outputAmount.asText());
                } catch (Exception ignored) {
                    LOGGER.log(Level.SEVERE, "Could not load amount for value: " + outputAmount.asText() + " | Defaulting to 1.");
                }
            }

            JsonNode shaped = json.get("shaped");
            boolean isShaped = shaped != null && shaped.asBoolean();
            if(isShaped) {
                ShapedRecipe recipe = new ShapedRecipe(heartKey, heartItem);
                JsonNode recipeArray = json.get("recipe");
                if(recipeArray != null && recipeArray.isArray()) {
                    String first = "", second = "", third = "";
                    int count = 0;
                    for(JsonNode row : recipeArray) {
                        switch (count) {
                            case 0 -> first = row.toString();
                            case 1 -> second = row.toString();
                            case 2 -> third = row.toString();
                        }
                        if(count == 2) break;
                        count++;
                    }

                    if(count == 0) return null;
                    if(count == 1) third = "   ";

                    first = first.replace("\"", "");
                    second = second.replace("\"", "");
                    third = third.replace("\"", "");

                    recipe.shape(first, second, third);
                }

                JsonNode itemObject = json.get("items");
                for (Iterator<Map.Entry<String, JsonNode>> iterator = itemObject.fields(); iterator.hasNext();) {
                    Map.Entry<String, JsonNode> entry = iterator.next();
                    JsonNode node = entry.getValue();
                    if(entry.getKey().length() == 0) continue;
                    if(node.has("item")) {
                        recipe.setIngredient(entry.getKey().charAt(0), Material.valueOf(node.get("item")
                                .asText().replaceFirst("minecraft:", "").replaceAll("\\s", "_").toUpperCase(Locale.ROOT)));
                    }
                }

                recipe.getResult().setAmount(amount);
                return recipe;
            }
            else if(shaped != null) {
                ShapelessRecipe recipe = new ShapelessRecipe(heartKey, heartItem);
                JsonNode itemObject = json.get("items");
                for (Iterator<Map.Entry<String, JsonNode>> iterator = itemObject.fields(); iterator.hasNext();) {
                    Map.Entry<String, JsonNode> entry = iterator.next();
                    JsonNode node = entry.getValue();
                    if(entry.getKey().length() == 0) continue;
                    if(node.has("item")) {
                        recipe.addIngredient(Material.valueOf(node.get("item")
                                .asText().replaceFirst("minecraft:", "").replaceAll("\\s", "_").toUpperCase(Locale.ROOT)));
                    }
                }
                recipe.getResult().setAmount(amount);
                return recipe;
            }
        }
        return null;
    }

    private void checkForUpdates() {
        InputStream is;
        try {
            is = getInputStream("http://188.34.178.99:8080/LSSMP/version");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not check for updates as the server is down/not responding!");
            return;
        }
        if(is == null) {
            LOGGER.log(Level.WARNING, "Could not establish input stream to update server. Not an error, however if this persists make an issue at https://www.github.com/IkeVoodoo/LSSMP/Issues.");
            return;
        }
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            if(!line.equalsIgnoreCase(getDescription().getVersion())) {
                broadcastMessage(prefix + ChatColor.GOLD + "There is a new update available! Current version: "
                        + ChatColor.RED + getDescription().getVersion()
                        + ChatColor.GOLD + ", Updated Version: "
                        + ChatColor.GREEN
                        + line, "lssmp.update.checker");
                updateAvailable = true;
            } else updateAvailable = false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not check for updates!");
        }
    }

    private static InputStream getInputStream(String website) throws IOException {
        URL url = new URL(website);

        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        HttpURLConnection.setFollowRedirects(false);
        huc.setConnectTimeout(5 * 1000);
        huc.setRequestMethod("GET");
        huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        huc.connect();
       try {
           return huc.getInputStream();
       } catch (SocketTimeoutException exception) {
            return null;
       }
    }

    private static void broadcastMessage(String message, String permission) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(player.hasPermission(permission) || player.isOp()) player.sendMessage(message);
        });
    }

}
