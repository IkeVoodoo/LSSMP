package me.ikevoodoo.lssmp.menus;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.menus.ItemData;
import me.ikevoodoo.smpcore.menus.MenuPage;
import me.ikevoodoo.smpcore.menus.PageData;
import me.ikevoodoo.smpcore.menus.functional.FunctionalMenu;
import me.ikevoodoo.smpcore.menus.functional.FunctionalPage;
import me.ikevoodoo.smpcore.recipes.RecipeData;
import me.ikevoodoo.smpcore.recipes.RecipeReplacement;
import me.ikevoodoo.smpcore.text.messaging.MessageBuilder;
import me.ikevoodoo.smpcore.utils.PDCUtils;
import me.ikevoodoo.smpcore.utils.Pair;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class RecipeEditor {

    private static final int HAS_PREV = 1;
    private static final int HAS_NEXT = 2;

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
        RecipeChoice[] choices = data.choices();
        functionalPage.edit(page -> {
            page.fill(plugin.getItem("empty").orElseThrow().getItemStack());

            if ((flags & HAS_NEXT) == HAS_NEXT) {
                page.last(plugin.getItem("next").orElseThrow().getItemStack());
            }

            if ((flags & HAS_PREV) == HAS_PREV) {
                page.first(5, plugin.getItem("prev").orElseThrow().getItemStack());
            }

            drawRecipe(choices, page, 0);

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
                    if (!item.hasRecipeFile()) {
                        MessageBuilder.messageOf("§4§lUnavailable! §r§cThe item does not support recipe editing!").send(player);
                        return;
                    }
                    plugin.getMenuHandler().get("lssmp_recipe_editor_" + item.getId()).open(player);
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

                    drawRecipe(item.getRecipeData().choices(), page, 1);

                    plugin
                            .createItem()
                            .id("confirm_recipe_editor_" + item.getId())
                            .friendlyName(MessageBuilder.messageOf("Confirm"))
                            .name(() -> MessageBuilder.messageOf("§a§lConfirm"))
                            .material(() -> Material.LIME_STAINED_GLASS_PANE)
                            .bind((player, stack) -> {
                                ItemStack[] stacks = getStacks(new ItemStack[9], page, player, 1);
                                RecipeReplacement[] replacements = getReplacements(plugin, stacks);
                                Material[] mats = Arrays.stream(stacks).map(ItemStack::getType).toArray(Material[]::new);
                                RecipeChoice[] choices = Arrays.stream(stacks).map(RecipeChoice.ExactChoice::new).toArray(RecipeChoice[]::new);
                                Recipe recipe = item.getRecipeData().recipe();
                                Recipe toWrite = null;
                                if (recipe instanceof ShapedRecipe shaped) {
                                    ShapedRecipe shapedRecipe = new ShapedRecipe(shaped.getKey(), shaped.getResult());
                                    shapedRecipe.shape("012", "345", "678");
                                    for (int i = 0; i < choices.length; i++) {
                                        shapedRecipe.setIngredient((i + "").charAt(0), choices[i]);
                                    }
                                    toWrite = shapedRecipe;
                                } else if (recipe instanceof ShapelessRecipe shapeless){
                                    ShapelessRecipe shapelessRecipe = new ShapelessRecipe(shapeless.getKey(), shapeless.getResult());
                                    for (RecipeChoice choice : choices) {
                                        shapelessRecipe.addIngredient(choice);
                                    }
                                }
                                if (toWrite != null)
                                    plugin.getRecipeLoader().writeRecipe(item.getRecipeFile(), new RecipeData(toWrite, mats, choices), replacements);
                                plugin.reload();
                                plugin.getMenuHandler().get("lssmp_recipe_settings_" + item.getId()).open(player);
                            })
                            .register();

                    page.last(plugin.getItem("confirm_recipe_editor_" + item.getId()).orElseThrow().getItemStack());
                })
                .done()
                .register();
    }

    private static void drawRecipe(RecipeChoice[] choices, MenuPage page, int offsetX) {
        for (int x = 0, width = choices.length / 3; x < width; x++) {
            for (int y = 0, height = choices.length / 3; y < height; y++) {
                RecipeChoice choice = choices[x + y * width];
                ItemStack stack = null;
                if (choice instanceof RecipeChoice.MaterialChoice mat)
                    stack = mat.getItemStack();
                else if (choice instanceof RecipeChoice.ExactChoice exactChoice)
                    stack = exactChoice.getItemStack();

                if (stack == null) continue;
                ItemMeta meta = stack.getItemMeta();
                if (meta != null) {
                    PersistentDataContainer pdc = meta.getPersistentDataContainer();
                    Set<NamespacedKey> copy = new HashSet<>(pdc.getKeys());
                    copy.forEach(pdc::remove);
                    stack.setItemMeta(meta);
                }

                page.item(ItemData.of((11 + x + offsetX) + (9 * y), stack));
            }
        }
    }

    private static ItemStack[] getStacks(ItemStack[] stacks, MenuPage page, Player player, int offsetX) {
        for (int x = 0, width = stacks.length / 3; x < width; x++) {
            for (int y = 0, height = stacks.length / 3; y < height; y++) {
                stacks[x + y * width] = page.item(player, (11 + x + offsetX) + (9 * y)).orElseGet(() -> new ItemStack(Material.AIR));
            }
        }
        return stacks;
    }

    private static RecipeReplacement[] getReplacements(SMPPlugin plugin, ItemStack... stacks) {
        List<RecipeReplacement> replacements = new ArrayList<>();
        int i = 1;
        for (ItemStack stack : stacks) {
            if(stack != null && !stack.getType().isAir()) {
                ItemMeta meta = stack.getItemMeta();
                if (meta == null) continue;
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                Optional<Pair<String, Byte>> optional = PDCUtils.get(pdc, PersistentDataType.BYTE);
                int finalI = i;
                optional.ifPresent(pair ->
                        plugin.getItem(pair.getFirst()).ifPresent(item ->
                                replacements.add(RecipeReplacement.of(finalI, pair.getFirst()))));
            }
            i++;
        }
        return replacements.toArray(new RecipeReplacement[0]);
    }

}
