package me.ikevoodoo.lssmp;

import dev.refinedtech.configlang.scope.Scope;
import me.ikevoodoo.juerr.Printer;
import me.ikevoodoo.juerr.UserError;
import me.ikevoodoo.lssmp.bstats.Metrics;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.lssmp.config.ResourepackConfig;
import me.ikevoodoo.lssmp.handlers.health.GlobalHealthHandler;
import me.ikevoodoo.lssmp.handlers.health.WorldHealthHandler;
import me.ikevoodoo.lssmp.language.Language;
import me.ikevoodoo.lssmp.language.YamlConfigSection;
import me.ikevoodoo.lssmp.menus.RecipeEditor;
import me.ikevoodoo.lssmp.menus.ReviveBeaconUI;
import me.ikevoodoo.lssmp.menus.SharedItems;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.callbacks.eliminations.EliminationType;
import me.ikevoodoo.smpcore.handlers.placeholders.PlaceholderHandler;
import me.ikevoodoo.smpcore.utils.ExceptionUtils;
import me.ikevoodoo.smpcore.utils.Lazy;
import me.ikevoodoo.smpcore.utils.StringUtils;
import me.ikevoodoo.smpcore.utils.ThreadUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class LSSMP extends SMPPlugin {

    public static final int CURRENT_CONFIG_VERSION = 8;
    private final Lazy<Language> lazyLanguage;

    public LSSMP() {
        this.lazyLanguage = new Lazy<>(() -> new Language(this));
    }

    @Override
    public void onPreload() {
        UserError.setExceptionHandler();

        saveResource("heartRecipe.yml", false);
        saveResource("beaconRecipe.yml", false);
        saveResource("heartFragmentRecipe.yml", false);
        saveResource("events.yml", false);
        new Metrics(this, 12177);
    }

    @Override
    public void whenEnabled() {
        this.loadHealthHandler();

        var logger = new Printer<>(getLogger()) {
            @Override
            public void printf(String s, Object... objects) {
                getOut().log(Level.SEVERE, s, objects);
            }

            @Override
            public void printfln(String message, Object... args) {
                this.printf(message + "\n", args);
            }
        };

        SharedItems.register(this);
        ReviveBeaconUI.createItems(this);

        if (isInstalled("PlaceholderAPI")) {
            PlaceholderHandler.create(this, "lssmp", "1.0.0")
                    .persist()
                    .onlineRequiresPlayer()
                    .online("raw_hearts", player -> String.valueOf(this.getHealthHelper().getMaxHearts(player)))
                    .online("hearts", player -> StringUtils.removeTrailingZeros(String.valueOf(this.getHealthHelper().getMaxHearts(player))))
                    .register();
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            List<UUID> toRevive = new ArrayList<>();
            getEliminationHandler().getEliminatedPlayers().forEach((uuid, time) -> {
                if(time.longValue() - System.currentTimeMillis() > 0) return;
                toRevive.add(uuid);
            });
            toRevive.forEach(uuid -> getEliminationHandler().reviveOffline(Bukkit.getOfflinePlayer(uuid)));
        }, 0, 20 * 5);

        getEliminationHandler().onCacheUpdated((uuid, number) -> {
            ReviveBeaconUI.createItems(this);
            ReviveBeaconUI.createMenus(this);
        });

        getResourcePackHandler().addResourcePack("pack", ResourepackConfig.getUrl());

        getEliminationHandler().listen(EliminationType.ELIMINATED, (eliminationType, player) -> {
            Scope scope = new Scope("elimination");
            scope.variables().set("player", new Object() {
                public final String name = player.getName();
                public final String displayName = player.getDisplayName();
                public final UUID uuid = player.getUniqueId();
            });

            this.getLanguage().execute(YamlConfigSection.of(
                    getConfigHandler()
                    .getYmlConfig("events.yml")
                    .getConfigurationSection("eliminated")), scope);
        });

        getEliminationHandler().listen(EliminationType.REVIVED, ((eliminationType, player) -> {
            if (MainConfig.Elimination.useReviveHearts) {
                getHealthHelper().setMaxHeartsEverywhere(player, MainConfig.Elimination.reviveHearts);
            }

            Scope scope = new Scope("revived");
            scope.variables().set("player", new Object() {
                public final String name = player.getName();
                public final String displayName = player.getDisplayName();
                public final UUID uuid = player.getUniqueId();
            });

            this.getLanguage().execute(YamlConfigSection.of(
                    getConfigHandler()
                    .getYmlConfig("events.yml")
                    .getConfigurationSection("revived")), scope);
        }));

        this.reload();
        if (!getConfig().contains("doNotTouch_configVersion") || MainConfig.doNotTouch_configVersion < CURRENT_CONFIG_VERSION) {
            UserError.from("You're using an outdated version of the config!")
                .addReason("The config version has changed")
                .addHelp("Run /lsupgrade (Will reset all of your configs and restart)")
                .addHelp("Make sure you don't change the option 'doNotTouch_configVersion' in the config")
                .printAll(logger, "LSSMP: ");
        }
    }

    @Override
    public void whenDisabled() {
        ThreadUtils.stop(0xD00D);
    }

    @Override
    public void onReload() {
        this.loadHealthHandler();

        RecipeEditor.createMenus(this);
        ReviveBeaconUI.createMenus(this);

        if(MainConfig.autoConfigReload) {
            try {
                WatchService service = FileSystems.getDefault().newWatchService();
                Path dir = getDataFolder().toPath();
                WatchKey key = dir.register(service, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                ThreadUtils.start(0xD00D, () -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        if(key.pollEvents().isEmpty()) continue;
                        Bukkit.getScheduler().callSyncMethod(this, () -> {
                            this.reload();
                            return true;
                        });
                    }
                    try {
                        service.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                getLogger().severe("Unable to use option 'autoConfigReload'");
                getLogger().severe("Go to https://pastebin.com and paste everything between the lines:");
                getLogger().severe("--------------------------");
                getLogger().severe(ExceptionUtils.asString(e));
                getLogger().severe("--------------------------");
                getLogger().severe("Once pasted, please click 'Create new Paste'");
                getLogger().severe("Then join https://refinedtech.dev/discord and create a ticket (check #ask-for-help)");
                getLogger().severe("And finally send the link in the extra info box that will be opened for you.");
            }
            return;
        }

        ThreadUtils.stop(0xD00D);
    }

    public Language getLanguage() {
        return this.lazyLanguage.get();
    }

    private void loadHealthHandler() {
        if (MainConfig.Elimination.perWorldHearts) {
            getHealthHelper().setHealthHandler(new WorldHealthHandler(world -> this.makeKey(world.getUID().toString())));
            return;
        }

        getHealthHelper().setHealthHandler(new GlobalHealthHandler());
    }

    @Override
    public void saveResource(@NotNull String resourcePath, boolean replace) {
        if (resourcePath.isBlank()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile());
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists() && !outDir.mkdirs()) {
            throw new IllegalStateException("Could not create path " + outDir);
        }

        if (outFile.exists() && !replace) {
            return;
        }

        try {
            Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save resource " + resourcePath + " to " + outFile, e);
        }
    }
}