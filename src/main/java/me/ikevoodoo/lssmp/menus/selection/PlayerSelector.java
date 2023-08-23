package me.ikevoodoo.lssmp.menus.selection;

import me.ikevoodoo.spigotcore.gui.Screen;
import me.ikevoodoo.spigotcore.gui.pages.PagePosition;
import me.ikevoodoo.spigotcore.gui.pages.PageType;
import me.ikevoodoo.spigotcore.gui.pages.ScreenPage;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PlayerSelector {

    private static final ItemStack NEXT_BUTTON;
    private static final ItemStack PREV_BUTTON;
    private static final ItemStack DISABLED;

    static {
        NEXT_BUTTON = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        setStackName(NEXT_BUTTON, "§a§lNext");

        PREV_BUTTON = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        setStackName(PREV_BUTTON, "§a§lPrevious");

        DISABLED = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        setStackName(DISABLED, " ");
    }

    private final Screen screen;
    private final Map<UUID, BiConsumer<HumanEntity, OfflinePlayer>> choosing = new HashMap<>();

    public PlayerSelector(Plugin plugin) {
        this.screen = new Screen(plugin, "Select a player.");
    }

    public void openFor(@NotNull HumanEntity player, BiConsumer<HumanEntity, OfflinePlayer> consumer) {
        if (this.screen.isOpen(player.getUniqueId())) {
            return;
        }

        this.choosing.put(player.getUniqueId(), consumer);
        this.screen.open(player);
    }

    public void setupPages(List<OfflinePlayer> players, Function<OfflinePlayer, ItemStack> playerToItem) {
        this.screen.clear();
        var count = players.size();

        if (count == 0) {
            this.setupPageDisplay(this.screen.createPage(PageType.chest(6)), 1);
            return;
        }

        var perPage = Math.min(9 * 5, count);

        var pageCount = (int) Math.ceil((double) count / perPage);

        this.screen.createPages(page -> {
            this.setupPageDisplay(page, pageCount);

            var offset = page.index() * perPage;
            var total = Math.min(count - offset, perPage);
            for (int i = 0; i < total; i++) {
                var player = players.get(offset + i);

                page.setItem(page.slotPosition(i), playerToItem.apply(player));
            }

            page.addClickHandler((event, stack, type) -> {
                var callback = choosing.get(event.player().getUniqueId());
                if (callback == null) return;

                if (!(stack.getItemMeta() instanceof SkullMeta skullMeta)) return;

                event.screen().close(event.player());

                var player = skullMeta.getOwningPlayer();
                if (player == null) return;

                callback.accept(event.player(), player);
            });
        }, PageType.chest(6), pageCount);
    }

    private void setupPageDisplay(ScreenPage page, int totalPages) {
        page.addButton(PagePosition.bottomLeft(), this.screen.shiftPageButton(-1, PREV_BUTTON, DISABLED));
        page.addButton(PagePosition.bottomRight(), this.screen.shiftPageButton(1, NEXT_BUTTON, DISABLED));

        for (int i = 0; i < 9; i++) {
            page.setItem(new PagePosition(i, 5), DISABLED);
        }

        page.setTitle(page.title() + " (" + (page.index() + 1) + " / " + totalPages + ")");
    }

    private static void setStackName(ItemStack stack, String displayName) {
        var meta = stack.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(displayName);

        stack.setItemMeta(meta);
    }

}
