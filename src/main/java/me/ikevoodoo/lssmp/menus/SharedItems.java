package me.ikevoodoo.lssmp.menus;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.text.messaging.MessageBuilder;
import org.bukkit.Material;

public class SharedItems {

    public static void register(SMPPlugin plugin) {
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

}
