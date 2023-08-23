package me.ikevoodoo.lssmp.menus.selection;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.text.messaging.MessageBuilder;
import me.ikevoodoo.smpcore.text.messaging.MessageProperty;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class PlayerReviveCallback implements BiConsumer<HumanEntity, OfflinePlayer> {

    private final SMPPlugin plugin;

    public PlayerReviveCallback(SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void accept(HumanEntity humanEntity, OfflinePlayer offline) {
        if (!(humanEntity instanceof Player player)) {
            throw new IllegalStateException("(Unexpected error) Expected a player! Got " + humanEntity.getClass());
        }

        if (!this.plugin.getEliminationHandler().isEliminated(offline.getUniqueId())) {
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

        this.plugin.getEliminationHandler().reviveOffline(offline);

        player.playEffect(EntityEffect.TOTEM_RESURRECT);
        player.sendTitle(" ", "§a§lRevived §c§l" + offline.getName(), 10, 20, 10);

        CustomItem.remove(player, plugin.getItem("revive_beacon").orElseThrow(), 1);
    }
}
