package me.ikevoodoo.lssmp.listeners;

import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.listeners.SMPListener;
import me.ikevoodoo.smpcore.utils.HealthUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerJoinListener extends SMPListener {
    public PlayerJoinListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        if(!event.getPlayer().hasPlayedBefore()) {
            HealthUtils.set(MainConfig.Elimination.defaultHearts * 2, event.getPlayer());
        }

        getPlugin().getResourcePackHandler().applyResourcePack(event.getPlayer(), "pack");

        String version = getPlugin().getDescription().getVersion();
        if (version.contains("Part") && !version.contains("Customization") && event.getPlayer().isOp()) {
            Player player = event.getPlayer();
            PersistentDataContainer container = player.getPersistentDataContainer();
            if (container.has(makeKey("version_msg"), PersistentDataType.STRING) &&
                    version.equals(container.get(makeKey("version_msg"), PersistentDataType.STRING))) {
                return;
            }
            container.set(makeKey("version_msg"), PersistentDataType.STRING, version);
            player.sendMessage("§cYou are using a Part version that is not complete, there won't be much customization.");
        }
    }
}
