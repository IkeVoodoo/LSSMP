package me.ikevoodoo.lssmp.menus;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.helpers.StringHelper;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.menus.ItemData;
import me.ikevoodoo.smpcore.menus.Menu;
import me.ikevoodoo.smpcore.menus.MenuPage;
import me.ikevoodoo.smpcore.menus.PageData;
import me.ikevoodoo.smpcore.menus.functional.FunctionalMenu;
import me.ikevoodoo.smpcore.menus.functional.FunctionalPage;
import me.ikevoodoo.smpcore.text.messaging.MessageBuilder;
import me.ikevoodoo.smpcore.text.messaging.MessageProperty;
import me.ikevoodoo.smpcore.utils.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ReviveBeaconUI {

    private static final int HAS_PREV = 1;
    private static final int HAS_NEXT = 2;

    private static final List<CustomItem> items = new ArrayList<>();

    private static final int[] i = {0};

    public static void createItems(SMPPlugin plugin) {
        items.forEach(plugin::destroyItem);
        items.clear();
        i[0] = 0;
        plugin.getEliminationHandler().getEliminatedPlayers()
                .forEach((uuid, revivedAt) -> {
                    OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
                    items.add(plugin.createItem()
                            .id("revive_" + i[0]++)
                            .friendlyName(MessageBuilder.messageOf("Revive"))
                            .name(() -> MessageBuilder
                                    .builderOf("&a&lRevive ")
                                    .add(offline.getName(), ChatColor.RED.asBungee()).build())
                            .item(() -> {
                                ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
                                SkullMeta meta = (SkullMeta) stack.getItemMeta();
                                meta.setOwningPlayer(offline);
                                meta.setLore(List.of("", "§a§lBanned for §c" + StringUtils.formatTime(revivedAt.longValue())));
                                stack.setItemMeta(meta);
                                return stack;
                            })
                            .bind((player, stack) -> {
                                if (!plugin.getEliminationHandler().isEliminated(uuid)) {
                                    MessageBuilder
                                            .create()

                                            .add("The player ", ChatColor.RED.asBungee())
                                            .properties(MessageProperty.BOLD)

                                            .add(offline.getName(), ChatColor.GREEN.asBungee())
                                            .properties(MessageProperty.BOLD)

                                            .add(" is not eliminated.", ChatColor.RED.asBungee())
                                            .properties(MessageProperty.BOLD)

                                            .build()
                                            .send(player);
                                    return;
                                }

                                plugin.getEliminationHandler().reviveOffline(offline);
                                player.closeInventory();
                                player.playEffect(EntityEffect.TOTEM_RESURRECT);
                                player.sendTitle(" ", "§a§lRevived §c§l" + offline.getName(), 10, 20, 10);

                                CustomItem.remove(player, plugin.getItem("revive_beacon").orElseThrow(), 1);
                            })
                            .register());

                });

        plugin.createItem()
                .id("revive_close")
                .friendlyName(MessageBuilder.messageOf("Close"))
                .name(() -> MessageBuilder.messageOf("&c&lClose"))
                .material(() -> Material.BARRIER)
                .bind((player, stack) -> player.closeInventory())
                .register();

        plugin.createItem()
                .id("search_by_name")
                .friendlyName(MessageBuilder.messageOf("Search"))
                .name(() -> MessageBuilder.messageOf("&c&lCan't find the player?"))
                .item(() -> {
                    ItemStack stack = new ItemStack(Material.OAK_SIGN);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setLore(List.of("", "§a§lSearch them by name!", "", "§4§l(Not available yet)"));
                    stack.setItemMeta(meta);
                    return stack;
                })
                .register();

        plugin.createItem()
                .id("sort_by_name")
                .friendlyName(MessageBuilder.messageOf("Sort"))
                .name(() -> MessageBuilder.messageOf("&c&lToo many players?"))
                .item(() -> {
                    ItemStack stack = new ItemStack(Material.SPYGLASS);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setLore(List.of("", "§a§lSort trough them!", "", "§4§l(Not available yet)"));
                    stack.setItemMeta(meta);
                    return stack;
                })
                .register();

    }

    public static void createMenus(SMPPlugin plugin) {
        HashMap<UUID, Number> eliminatedPlayers = plugin.getEliminationHandler().getEliminatedPlayers();
        int requiredPages = Math.max((int) Math.ceil(eliminatedPlayers.size() / 28.0), 1);
        List<Integer> pageNumbers = IntStream.range(0, requiredPages).boxed().toList();

        plugin.destroyMenu("lssmp_revive_beacon_menu");

        //int minLength = getMinLength(1, pageNumbers);

        plugin.createMenu()
                .id("lssmp_revive_beacon_menu")
                .each(pageNumbers)
                .withPriority(0, (menu, index) ->
                        fillPage(plugin, ((FunctionalMenu)menu).page(PageData.of(9 * 6,
                                MessageBuilder.messageOf(
                                        StringHelper
                                                .from("§8%s/%s".formatted(index + 1, requiredPages))
                                                .prefix("                  ")
                                                .postfix("                  ")
                                                .middle(40, 2)
                                                .toString()
                                )
                        )), index, index == 0 && requiredPages > 1 ? HAS_NEXT : 0)
                )
                .withPriority(-1, (menu, index) ->
                        fillPage(plugin, ((FunctionalMenu)menu).page(PageData.of(9 * 6,
                                MessageBuilder.messageOf(
                                        StringHelper
                                                .from("§8%s/%s".formatted(index + 1, requiredPages))
                                                .prefix("                  ")
                                                .postfix("                  ")
                                                .middle(40, 2)
                                                .toString()
                                )
                        )), index, index > 0 ? HAS_PREV : 0)
                )
                .with((menu, index) ->
                        fillPage(plugin, ((FunctionalMenu)menu).page(PageData.of(9 * 6,
                                MessageBuilder.messageOf(
                                        StringHelper
                                                .from("§8%s/%s".formatted(index + 1, requiredPages))
                                                .prefix("                  ")
                                                .postfix("                  ")
                                                .middle(40, 2)
                                                .toString()
                                )
                        )), index, HAS_NEXT | HAS_PREV)
                )
                .<FunctionalMenu>execute()
                .register();
    }

    private static void fillPage(SMPPlugin plugin, FunctionalPage functionalPage, int pageId, int flags) {
        functionalPage.edit(page -> {
            page.onOpen(player -> {
                UUID id = player.getUniqueId();
                AtomicInteger i = new AtomicInteger(0);
                i.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    Player plr = Bukkit.getPlayer(id);
                    if (plr == null) {
                        Bukkit.getScheduler().cancelTask(i.get());
                        return;
                    }

                    Menu menu = plugin.getMenuHandler().get(plr);
                    if (menu == null) {
                        Bukkit.getScheduler().cancelTask(i.get());
                        return;
                    }

                    Optional<MenuPage> optional = menu.page(plr);
                    if (optional.isEmpty()) {
                        Bukkit.getScheduler().cancelTask(i.get());
                        return;
                    }

                    MenuPage menuPage = optional.get();
                    if(menuPage != page) {
                        Bukkit.getScheduler().cancelTask(i.get());
                        return;
                    }

                    List<ItemData> items = new ArrayList<>();
                    AtomicInteger slotOffset = new AtomicInteger();
                    menuPage.items(player)
                            .forEach(data -> {
                                ItemStack stack = data.getStack();
                                ItemMeta meta = stack.getItemMeta();
                                if(!(meta instanceof SkullMeta skullMeta)) {
                                    return;
                                }

                                OfflinePlayer owner = skullMeta.getOwningPlayer();
                                if(owner == null) {
                                    return;
                                }

                                if(!plugin.getEliminationHandler().isEliminated(owner.getUniqueId())) {
                                    slotOffset.getAndIncrement();
                                    return;
                                }

                                long unbannedAt = plugin.getEliminationHandler().getEliminatedPlayers().get(owner.getUniqueId()).longValue();
                                if(unbannedAt == 0) {
                                    slotOffset.getAndIncrement();
                                    return;
                                }
                                String time = StringUtils.formatTime(unbannedAt);
                                if (time.equalsIgnoreCase("now")) {
                                    slotOffset.getAndIncrement();
                                    return;
                                }
                                skullMeta.setLore(List.of("", "§a§lBanned for §c" + time));
                                stack.setItemMeta(meta);
                                items.add(ItemData.of(data.slot() - slotOffset.get(), stack));
                            });
                    page.item(items.toArray(new ItemData[0]));
                }, 0, 20));
            });

            page.fill(plugin.getItem("empty").orElseThrow().getItemStack());

            if ((flags & HAS_NEXT) == HAS_NEXT) {
                page.last(plugin.getItem("next").orElseThrow().getItemStack());
            }

            if ((flags & HAS_PREV) == HAS_PREV) {
                page.first(6, plugin.getItem("prev").orElseThrow().getItemStack());
            }

            // loop from 0 to 54, and set all items to stone (add one border, so start from 10, to 17, then 19 to 26, then 28 to 35, then 37 to 44)
            for (int row = 1; row < 5; row++) {
                for (int col = 1; col < 8; col++) {
                    int slot = row * 9 + col;
                    // subtract so slot(10) is 0
                    int index = (pageId * 28) + (slot - 10);
                    if (index < items.size()) {
                        page.item(ItemData.of(slot, items.get(index).getItemStack()));
                    } else {
                        page.item(ItemData.of(slot, new ItemStack(Material.AIR)));
                    }
                }
            }

            page.item(
                    ItemData.of(48, plugin.getItem("search_by_name").orElseThrow().getItemStack()),
                    //ItemData.of(49, plugin.getItem("revive_close").orElseThrow().getItemStack()),
                    ItemData.of(50, plugin.getItem("sort_by_name").orElseThrow().getItemStack())
            );
        });
    }

}
