package me.ikevoodoo.lssmp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class PlayerChatListener implements Listener {

    private final Map<UUID, BiConsumer<Player, String>> messageConsumer;

    public PlayerChatListener(Map<UUID, BiConsumer<Player, String>> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        var player = event.getPlayer();
        var consumer = this.messageConsumer.remove(player.getUniqueId());

        if (consumer != null) {
            consumer.accept(player, event.getMessage());
            event.setCancelled(true);
        }
    }
}
