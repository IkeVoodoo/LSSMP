package me.ikevoodoo.lssmp;

import dev.refinedtech.configlang.scope.Scope;
import me.ikevoodoo.Printer;
import me.ikevoodoo.UserError;
import me.ikevoodoo.lssmp.bstats.Metrics;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.lssmp.config.ResourepackConfig;
import me.ikevoodoo.lssmp.language.Language;
import me.ikevoodoo.lssmp.language.YamlConfigSection;
import me.ikevoodoo.lssmp.menus.RecipeEditor;
import me.ikevoodoo.lssmp.menus.ReviveBeaconUI;
import me.ikevoodoo.lssmp.menus.SharedItems;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.callbacks.eliminations.EliminationType;
import me.ikevoodoo.smpcore.handlers.placeholders.PlaceholderHandler;
import me.ikevoodoo.smpcore.utils.*;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public final class LSSMP extends SMPPlugin {

    public static final int CURRENT_CONFIG_VERSION = 5;
    private static Lazy<Language> LANGUAGE;

    private static Printer<Logger> LOGGER;

    @Override
    public void onPreload() {
        UserError.setExceptionHandler();

        LANGUAGE = new Lazy<>(() -> new Language(this));

        saveResource("heartRecipe.yml", false);
        saveResource("beaconRecipe.yml", false);
        new Metrics(this, 12177);
    }

    @Override
    public void whenEnabled() {
        LOGGER = new Printer<>(getLogger()) {
            @Override
            public void printf(String s, Object... objects) {
                getOut().severe(String.format(s, objects));
            }

            @Override
            public void printfln(String message, Object... args) {
                this.printf(message, args);
            }
        };

        SharedItems.register(this);
        ReviveBeaconUI.createItems(this);

        if (isInstalled("PlaceholderAPI")) {
            PlaceholderHandler.create(this, "lssmp", "1.0.0")
                    .persist()
                    .onlineRequiresPlayer()
                    .online("raw_hearts", player -> String.valueOf(HealthUtils.get(player) / 2))
                    .online("hearts", player -> StringUtils.removeTrailingZeros(String.valueOf(HealthUtils.get(player) / 2)))
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

            LSSMP.getLanguage().execute(YamlConfigSection.of(
                    getConfigHandler()
                    .getYmlConfig("events.yml")
                    .getConfigurationSection("events")
                    .getConfigurationSection("eliminated")), scope);
        });

        getEliminationHandler().listen(EliminationType.REVIVED, ((eliminationType, player) -> {
            if (MainConfig.Elimination.useReviveHearts) {
                HealthUtils.setAll(MainConfig.Elimination.reviveHearts, player, this);
            }

            Scope scope = new Scope("revived");
            scope.variables().set("player", new Object() {
                public final String name = player.getName();
                public final String displayName = player.getDisplayName();
                public final UUID uuid = player.getUniqueId();
            });

            LSSMP.getLanguage().execute(YamlConfigSection.of(
                    getConfigHandler()
                    .getYmlConfig("events.yml")
                    .getConfigurationSection("events")
                    .getConfigurationSection("revived")), scope);
        }));

        this.reload();
        if (!getConfig().contains("doNotTouch_configVersion") || MainConfig.doNotTouch_configVersion < CURRENT_CONFIG_VERSION) {
            UserError.from("You're using an outdated version of the config!")
                .addReason("The config version has changed")
                .addHelp("Run /lsupgrade (Will reset all of your configs and restart)")
                .addHelp("Make sure you don't change the option 'doNotTouch_configVersion' in the config")
                .printAll(LOGGER, "LSSMP: ");
        }
    }

    @Override
    public void whenDisabled() {
        ThreadUtils.stop(0xD00D);
    }

    @Override
    public void onReload() {
        //createMenus();
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

    public static Language getLanguage() {
        return LANGUAGE.get();
    }
}