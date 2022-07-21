package me.ikevoodoo.lssmp;

import me.ikevoodoo.Printer;
import me.ikevoodoo.UserError;
import me.ikevoodoo.lssmp.bstats.Metrics;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.lssmp.menus.RecipeEditor;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.handlers.placeholders.PlaceholderHandler;
import me.ikevoodoo.smpcore.utils.ExceptionUtils;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import me.ikevoodoo.smpcore.utils.ThreadUtils;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;

public final class LSSMP extends SMPPlugin {

    public static final int CURRENT_CONFIG_VERSION = 5;

    private static Printer<Logger> LOGGER;

    @Override
    public void onPreload() {
        UserError.setExceptionHandler();

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

        RecipeEditor.createItems(this);

        if (isInstalled("PlaceholderAPI")) {
            PlaceholderHandler.create(this, "lssmp", "1.0.0")
                    .persist()
                    .onlineRequiresPlayer()
                    .online("hearts", player -> String.valueOf(HealthUtils.get(player) / 2))
                    .register();
        }

        this.reload();
        if (!getConfig().contains("doNotTouch_configVersion") || MainConfig.doNotTouch_configVersion < CURRENT_CONFIG_VERSION) {
            UserError.from("You're using an outdated version of the config!")
                .addReason("The config version has changed")
                .addHelp("Run /lsupgrade (Will reset all of your configs and restart)")
                .addHelp("Make sure you don't change the option 'doNotTouch_configVersion' in the config")
                .printAll(LOGGER, "LSSMP: ");
            /*getLogger().severe("========== LSSMP ==========");
            getLogger().severe("You are using an outdated version of the config!");
            getLogger().severe("To fix this run /lsupgrade");
            getLogger().severe("WARNING: RUNNING /lsupgrade WILL RESET ALL OF YOUR CONFIGS AND RESTART THE SERVER, PROCEED WITH CAUTION");*/
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
}