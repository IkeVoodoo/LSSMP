package me.ikevoodoo.lssmp;

import me.ikevoodoo.lssmp.bstats.Metrics;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.menus.ItemData;
import me.ikevoodoo.smpcore.menus.Menu;
import me.ikevoodoo.smpcore.menus.PageData;
import me.ikevoodoo.smpcore.recipes.RecipeData;
import me.ikevoodoo.smpcore.utils.ExceptionUtils;
import me.ikevoodoo.smpcore.utils.ThreadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public final class LSSMP extends SMPPlugin {

    public static final int CURRENT_CONFIG_VERSION = 3;

    public static NamespacedKey LSSMP_MENU;

    @Override
    public void onPreload() {
        saveResource("heartRecipe.yml", false);
        saveResource("beaconRecipe.yml", false);
        new Metrics(this, 12177);

        LSSMP_MENU = new NamespacedKey(this, "lssmp_menu");
    }

    @Override
    public void whenEnabled() {
        this.reload();
        if (!getConfig().contains("doNotTouch_configVersion") || MainConfig.doNotTouch_configVersion < CURRENT_CONFIG_VERSION) {
            getLogger().severe("========== LSSMP ==========");
            getLogger().severe("You are using an outdated version of the config!");
            getLogger().severe("To fix this run /lsupgrade");
            getLogger().severe("WARNING: RUNNING /lsupgrade WILL RESET ALL OF YOUR CONFIGS AND RESTART THE SERVER, PROCEED WITH CAUTION");
        }
    }

    @Override
    public void whenDisabled() {
        ThreadUtils.stop(0xD00D);
    }

    @Override
    public void onReload() {
        createMenus();

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
        Menu heart = new Menu(LSSMP_MENU);
        List<CustomItem> items = getItems();
        if (items.size() > 0) {
            CustomItem first = items.get(0);
            heart.page(PageData.of(9 * 5, first.getFriendlyName())).item(getData(first, false, true));
            for (int i = 1; i < items.size() - 1; i++) {
                CustomItem item = items.get(i);
                heart.page(PageData.of(9 * 5, item.getFriendlyName())).item(getData(item, true, true));
            }
            CustomItem last = items.get(items.size() - 1);
            if (!last.equals(first)) {
                heart.page(PageData.of(9 * 5, last.getFriendlyName())).item(getData(last, true, false));
            }
        }
        getMenuHandler().add(heart);
    }

    private ItemData[] getData(CustomItem item, boolean hasPrev, boolean hasNext) {
        RecipeData data = item.getRecipeData();
        ItemData[] items = new ItemData[9 * 5];
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = stack.getItemMeta();
            if (meta != null)
                meta.setDisplayName(" ");
            stack.setItemMeta(meta);
            items[i] = ItemData.of(i, stack);
        }

        if (hasNext) {
            ItemStack stack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta meta = stack.getItemMeta();
            if (meta != null)
                meta.setDisplayName("§a§lNext");
            stack.setItemMeta(meta);
            items[9 * 5 - 1] = ItemData.of(9 * 5 - 1, stack);
        }

        if (hasPrev) {
            ItemStack stack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta meta = stack.getItemMeta();
            if (meta != null)
                meta.setDisplayName("§a§lPrevious");
            stack.setItemMeta(meta);
            items[9 * 4] = ItemData.of(9 * 4, stack);
        }

        for (int x = 0, width = data.materials().length / 3; x < width; x++) {
            for (int y = 0, height = data.materials().length / 3; y < height; y++) {
                items[(11 + x) + (9 * y)] = ItemData.of((11 + x) + (9 * y), new ItemStack(data.materials()[x + y * width]));
            }
        }

        items[24] = ItemData.of(24, item.getItemStack());

        return items;
    }
}