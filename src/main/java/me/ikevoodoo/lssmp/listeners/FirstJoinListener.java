package me.ikevoodoo.lssmp.listeners;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.config.Configuration;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class FirstJoinListener implements Listener {

    private final Configuration generalConfig;
    private final NamespacedKey initKey;

    public FirstJoinListener(Configuration generalConfig, NamespacedKey initKey) {
        this.generalConfig = generalConfig;
        this.initKey = initKey;
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("deprecation")
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        var oldKey = new NamespacedKey("lifesteal-smp-plugin", "eliminated_player");

        var pdc = player.getPersistentDataContainer();

        if (pdc.has(this.initKey, PersistentDataType.BYTE)) {
            return;
        }

        if (pdc.has(oldKey, PersistentDataType.BYTE)) {
            pdc.remove(oldKey);

            var tag = Helix.tags().get("elimination");
            tag.add(player.getUniqueId(), (uuid, storage) -> {});
            return;
        }

        if (player.isOp()) {
            this.sendWelcome(player);
        }

        pdc.set(this.initKey, PersistentDataType.BYTE, (byte) 1);

        double max = this.generalConfig.getValue("defaultHearts");

        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(max * 2);
        player.setHealth(max * 2);

    }

    private void sendWelcome(Player player) {
        player.sendMessage("§aWelcome to §cLifeSteal §3v3.0.0§a! Here you can find all of the info you need.");
        player.sendMessage("§aTo get started, run the command §f/lssetup §ato setup your server.");
        player.sendMessage("§aThe config is found by navigating through the following folders:");
        player.sendMessage("§fplugins §7-> §flifesteal");
        player.sendMessage("§r");
        player.sendMessage("§aThe folder named §fplugins §ais the same folder where you install your plugins.");
        player.sendMessage("§aThe folder named §flifesteal §ais different from the file §flifesteal.jar");
        player.sendMessage("§a§nDo not try to open the file §flifesteal.jar §aas you cannot edit that!");
        player.sendMessage("§r");
        player.sendMessage("§aYou will be informed of plugin updates in-game and will be able to install them with a single click.");
        player.sendMessage("§aIf you feel like some features are missing, it may be because there are a few minor updates left, so watch out for those messages!");
        player.sendMessage("§aTo reload the plugin, you can use §f/helix reload lifesteal");
        player.sendMessage("§r");
        player.sendMessage("§6If the message is cut off, you can scroll up in the chat for extra info!");
        player.sendMessage("§aThank you for using §3Refined Tech §asoftware.");
    }

}
