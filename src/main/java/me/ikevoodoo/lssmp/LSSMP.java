package me.ikevoodoo.lssmp;

import me.ikevoodoo.Printer;
import me.ikevoodoo.UserError;
import me.ikevoodoo.lssmp.bstats.Metrics;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.lssmp.menus.RecipeEditor;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.handlers.placeholders.PlaceholderHandler;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.menus.ItemData;
import me.ikevoodoo.smpcore.menus.PageData;
import me.ikevoodoo.smpcore.menus.functional.FunctionalMenu;
import me.ikevoodoo.smpcore.recipes.RecipeData;
import me.ikevoodoo.smpcore.text.messaging.MessageBuilder;
import me.ikevoodoo.smpcore.utils.ExceptionUtils;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import me.ikevoodoo.smpcore.utils.ThreadUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;

public final class LSSMP extends SMPPlugin {

    public static final int CURRENT_CONFIG_VERSION = 4;

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

    private void createMenus() {
        createMenu()
                .id("lssmp_menu")
                .each(getItems())
                .filter(item -> item.getRecipeData() == null)
                .withPriority(0, (menu, item) -> ((FunctionalMenu)menu).page(PageData.of(9 * 5, item.getFriendlyName()))
                        .edit(page -> page.item(getData(item, false, true))))
                .withPriority(-1, (menu, item) -> ((FunctionalMenu)menu).page(PageData.of(9 * 5, item.getFriendlyName()))
                        .edit(page -> page.item(getData(item, true, false))))
                .with((menu, item) -> ((FunctionalMenu)menu).page(PageData.of(9 * 5, item.getFriendlyName()))
                        .edit(page -> page.item(getData(item, true, true))))
                .<FunctionalMenu>execute()
                .register();
    }

    private ItemData[] getData(CustomItem item, boolean hasPrev, boolean hasNext) {
        RecipeData data = item.getRecipeData();
        ItemData[] items = new ItemData[9 * 5];
        ItemStack empty = getItem("empty").orElseThrow().getItemStack();
        for (int i = 0; i < items.length; i++)
            items[i] = ItemData.of(i, empty);

        if (hasNext) {
            items[9 * 5 - 1] = ItemData.of(9 * 5 - 1, getItem("next").orElseThrow().getItemStack());
        }

        if (hasPrev) {
            items[9 * 4] = ItemData.of(9 * 4, getItem("prev").orElseThrow().getItemStack());
        }

        for (int x = 0, width = data.materials().length / 3; x < width; x++) {
            for (int y = 0, height = data.materials().length / 3; y < height; y++) {
                items[(11 + x) + (9 * y)] = ItemData.of((11 + x) + (9 * y), new ItemStack(data.materials()[x + y * width]));
            }
        }

        items[24] = ItemData.of(24, item.getCleanStack());

        CustomItem custom = createItem()
                .id("lssmp_item_settings_" + item.getId())
                .friendlyName(MessageBuilder.messageOf("§c§lSettings"))
                .name(() -> MessageBuilder.messageOf("§c§lSettings"))
                .material(() -> Material.COMPARATOR)
                .bind((player, stack) -> getMenuHandler().get("lssmp_settings_" + item.getId()).open(player))
                .register();

        createSettingsMenu(item, custom);

        items[8] = ItemData.of(8, custom.getItemStack());

        return items;
    }

    private void createSettingsMenu(CustomItem item, CustomItem settingsItem) {
        CustomItem recipeEditorItem = createItem()
                .id("recipe_editor_" + item.getId())
                .friendlyName(MessageBuilder.messageOf("&6&lRecipe Editor"))
                .name(() -> MessageBuilder.messageOf("&6&lRecipe Editor"))
                .material(() -> Material.CRAFTING_TABLE)
                .bind((player, stack) -> getMenuHandler().get("lssmp_recipe_editor_" + item.getId()).open(player))
                .register();

        createMenu()
                .id("lssmp_settings_" + item.getId())
                .page(PageData.of(9 * 5, MessageBuilder.messageOf(settingsItem.getFriendlyName())))
                .edit(page -> {
                    page.fill(getItem("empty").orElseThrow().getItemStack());

                    page.item(ItemData.of(
                            20,
                            recipeEditorItem.getItemStack()
                    ));

                    createItem()
                            .id("toggle_" + item.getId())
                            .friendlyName(MessageBuilder.messageOf("Toggle"))
                            .name(() -> item.isEnabled() ? MessageBuilder.messageOf("§c§lDisable") : MessageBuilder.messageOf("§a§lEnable"))
                            .material(() -> item.isEnabled() ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE)
                            .lore(() -> MessageBuilder.builderOf("§6§lCurrently: ").add(
                                    item.isEnabled() ? "Enabled" : "Disabled",
                                    item.isEnabled() ? ChatColor.GREEN : ChatColor.RED
                            ).build())
                            .bind((player, stack) -> {
                                item.setEnabled(!item.isEnabled());
                                page.item(ItemData.of(24, getItem("toggle_" + item.getId()).orElseThrow().getItemStack()));
                            })
                            .register();

                    createItem()
                            .id("settings_" + item.getId() + "_back")
                            .friendlyName(MessageBuilder.messageOf("Back"))
                            .name(() -> MessageBuilder.messageOf("§c§lBack"))
                            .material(() -> Material.RED_STAINED_GLASS_PANE)
                            .bind((player, stack) -> getMenuHandler().get("lssmp_menu").openLast(player))
                            .register();

                    page.item(ItemData.of(9 * 4, getItem("settings_" + item.getId() + "_back").orElseThrow().getItemStack()));

                    page.onOpen(player -> {
                        System.out.println("Opened settings for " + player.getName());
                        /*page.item(player, ItemData.of(
                                24,
                                getItem("toggle_" + item.getId()).orElseThrow().getItemStack()
                        ));*/
                    });
                })
                .done()
                .register();

        createMenu()
                .id("lssmp_recipe_editor_" + item.getId())
                .page(PageData.of(9 * 5, MessageBuilder.messageOf(recipeEditorItem.getFriendlyName())))
                .edit(page -> {
                    page.fill(getItem("empty").orElseThrow().getItemStack());

                    RecipeData data = item.getRecipeData();

                    for (int x = 0, width = data.materials().length / 3; x < width; x++) {
                        for (int y = 0, height = data.materials().length / 3; y < height; y++) {
                            page.item(ItemData.of((11 + x) + (9 * y), new ItemStack(data.materials()[x + y * width])));
                        }
                    }

                    createItem()
                            .id("confirm_recipe_editor_" + item.getId())
                            .friendlyName(MessageBuilder.messageOf("Confirm"))
                            .name(() -> MessageBuilder.messageOf("§a§lConfirm"))
                            .material(() -> Material.LIME_STAINED_GLASS_PANE)
                            .bind((player, stack) -> getMenuHandler().get("lssmp_settings_" + item.getId()).open(player))
                            .register();

                    page.item(ItemData.of(-1, getItem("confirm_recipe_editor_" + item.getId()).orElseThrow().getItemStack()));
                })
                .done()
                .register();
    }
}