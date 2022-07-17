package me.ikevoodoo.lssmp.menus;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.menus.ItemData;
import me.ikevoodoo.smpcore.menus.MenuPage;
import me.ikevoodoo.smpcore.menus.PageData;
import me.ikevoodoo.smpcore.menus.functional.FunctionalMenu;
import me.ikevoodoo.smpcore.menus.functional.FunctionalPage;
import me.ikevoodoo.smpcore.recipes.RecipeData;
import me.ikevoodoo.smpcore.text.messaging.MessageBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RecipeEditor {

    private static final int HAS_PREV = 1;
    private static final int HAS_NEXT = 2;

    public static void createItems(SMPPlugin plugin) {
        plugin.createItem()
                .id("empty")
                .friendlyName(MessageBuilder.messageOf("Empty"))
                .name(() -> MessageBuilder.messageOf(" "))
                .material(() -> Material.GRAY_STAINED_GLASS_PANE)
                .register();

        plugin.createItem()
                .id("next")
                .friendlyName(MessageBuilder.messageOf("Next"))
                .name(() -> MessageBuilder.messageOf("§a§lNext"))
                .material(() -> Material.LIME_STAINED_GLASS_PANE)
                .bind((player, stack) -> plugin.getMenuHandler().get(player).next(player))
                .register();

        plugin.createItem()
                .id("prev")
                .friendlyName(MessageBuilder.messageOf("Prev"))
                .name(() -> MessageBuilder.messageOf("§a§lPrevious"))
                .material(() -> Material.LIME_STAINED_GLASS_PANE)
                .bind((player, stack) -> plugin.getMenuHandler().get(player).previous(player))
                .register();
    }

    public static void createMenus(SMPPlugin plugin) {
        plugin.createMenu()
                .id("lssmp_recipes_menu")
                .each(plugin.getItems())
                .filter(item -> item.getRecipeData() == null)
                .withPriority(0, (menu, item) ->
                        editRecipePage(plugin, ((FunctionalMenu)menu).page(PageData.of(9 * 5, item.getFriendlyName())), item, HAS_NEXT))
                .withPriority(-1, (menu, item) ->
                        editRecipePage(plugin, ((FunctionalMenu)menu).page(PageData.of(9 * 5, item.getFriendlyName())), item, HAS_PREV))
                .with((menu, item) ->
                        editRecipePage(plugin, ((FunctionalMenu)menu).page(PageData.of(9 * 5, item.getFriendlyName())), item, HAS_NEXT | HAS_PREV))
                .<FunctionalMenu>execute()
                .register();
    }

    private static void editRecipePage(SMPPlugin plugin, FunctionalPage functionalPage, CustomItem item, int flags) {
        RecipeData data = item.getRecipeData();
        Material[] materials = data.materials();
        functionalPage.edit(page -> {
            page.fill(plugin.getItem("empty").orElseThrow().getItemStack());

            if ((flags & HAS_NEXT) == HAS_NEXT) {
                page.last(plugin.getItem("next").orElseThrow().getItemStack());
            }

            if ((flags & HAS_PREV) == HAS_PREV) {
                page.first(5, plugin.getItem("prev").orElseThrow().getItemStack());
            }

            drawRecipe(materials, page, 0);

            page.item(ItemData.of(24, item.getCleanStack()));

            createSettings(plugin, page, item);
        });
    }

    private static void createSettings(SMPPlugin plugin, MenuPage recipePage, CustomItem item) {
        CustomItem settingsItem = plugin.createItem()
                .id("lssmp_recipe_settings_item_" + item.getId())
                .friendlyName(MessageBuilder.messageOf("§c§lSettings"))
                .name(() -> MessageBuilder.messageOf("§c§cSettings"))
                .material(() -> Material.COMPARATOR)
                .bind((player, stack) -> {
                    if (player.hasPermission("lssmp.recipe.settings")) {
                        plugin.getMenuHandler().get("lssmp_recipe_settings_" + item.getId()).open(player);
                    }
                })
                .register();

        recipePage.onOpen(player -> {
            if (player.hasPermission("lssmp.recipe.settings"))
                recipePage.item(player, ItemData.of(8, settingsItem.getItemStack()));
        });

        CustomItem recipeEditorItem = plugin.createItem()
                .id("recipe_editor_" + item.getId())
                .friendlyName(MessageBuilder.messageOf("§6§lRecipe Editor"))
                .name(() -> MessageBuilder.messageOf("§6§lRecipe Editor"))
                .material(() -> Material.CRAFTING_TABLE)
                .bind(((player, stack) -> {
                    MessageBuilder.messageOf("§4§lUnavailable! §r§cThis feature is still being worked on!");
                    // plugin.getMenuHandler().get("lssmp_recipe_editor_" + item.getId()).open(player);
                }))
                .register();

        plugin
                .createMenu()
                .id("lssmp_recipe_settings_" + item.getId())
                .page(PageData.of(9 * 5, MessageBuilder.messageOf(settingsItem.getFriendlyName())))
                .edit(page -> {
                    page.fill(plugin.getItem("empty").orElseThrow().getItemStack());

                    page.item(ItemData.of(20, recipeEditorItem.getItemStack()));

                    plugin.createItem()
                            .id("toggle_" + item.getId())
                            .friendlyName(MessageBuilder.messageOf("Toggle"))
                            .name(() -> item.isEnabled() ? MessageBuilder.messageOf("§a§lEnabled") : MessageBuilder.messageOf("§c§lDisabled"))
                            .material(() -> item.isEnabled() ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                            .lore(() -> MessageBuilder.builderOf("§6§lCick to ").add(
                                    item.isEnabled() ? "Disable" : "Enable",
                                    item.isEnabled() ? ChatColor.RED : ChatColor.GREEN
                            ).build())
                            .bind((player, stack) -> {
                                item.setEnabled(!item.isEnabled());
                                page.item(ItemData.of(24, plugin.getItem("toggle_" + item.getId()).orElseThrow().getItemStack()));
                            })
                            .register();

                    plugin.createItem()
                            .id("settings_" + item.getId() + "_back")
                            .friendlyName(MessageBuilder.messageOf("Back"))
                            .name(() -> MessageBuilder.messageOf("§c§lBack"))
                            .material(() -> Material.RED_STAINED_GLASS_PANE)
                            .bind((player, stack) -> plugin.getMenuHandler().get("lssmp_recipes_menu").openLast(player))
                            .register();

                    page.first(5, plugin.getItem("settings_" + item.getId() + "_back").orElseThrow().getItemStack());

                    page.onOpen(player ->
                            page.item(player, ItemData.of(24, plugin.getItem("toggle_" + item.getId()).orElseThrow().getItemStack())));
                })
                .done()
                .register();

        plugin
                .createMenu()
                .id("lssmp_recipe_editor_" + item.getId())
                .page(PageData.of(9 * 5, MessageBuilder.messageOf(recipeEditorItem.getFriendlyName())))
                .edit(page -> {
                    page.fill(plugin.getItem("empty").orElseThrow().getItemStack());

                    drawRecipe(item.getRecipeData().materials(), page, 1);

                    plugin
                            .createItem()
                            .id("confirm_recipe_editor_" + item.getId())
                            .friendlyName(MessageBuilder.messageOf("Confirm"))
                            .name(() -> MessageBuilder.messageOf("§a§lConfirm"))
                            .material(() -> Material.LIME_STAINED_GLASS_PANE)
                            .bind((player, stack) -> plugin.getMenuHandler().get("lssmp_settings_" + item.getId()).open(player))
                            .register();

                    page.last(plugin.getItem("confirm_recipe_editor_" + item.getId()).orElseThrow().getItemStack());
                })
                .done()
                .register();
    }

    private static void drawRecipe(Material[] materials, MenuPage page, int offsetX) {
        for (int x = 0, width = materials.length / 3; x < width; x++) {
            for (int y = 0, height = materials.length / 3; y < height; y++) {
                page.item(ItemData.of((11 + x + offsetX) + (9 * y), new ItemStack(materials[x + y * width])));
            }
        }
    }

}
