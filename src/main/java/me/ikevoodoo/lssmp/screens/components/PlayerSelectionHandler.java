package me.ikevoodoo.lssmp.screens.components;

import com.google.common.util.concurrent.AtomicDouble;
import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.helix.api.messages.MessageBuilder;
import me.ikevoodoo.helix.api.messages.colors.MinecraftColor;
import me.ikevoodoo.helix.api.namespaced.UniqueIdentifier;
import me.ikevoodoo.helix.api.screens.components.HelixComponentContext;
import me.ikevoodoo.helix.api.screens.components.HelixComponentEvent;
import me.ikevoodoo.helix.api.screens.components.HelixComponentHandler;
import me.ikevoodoo.helix.api.screens.components.HelixPageComponent;
import me.ikevoodoo.lssmp.Constants;
import me.ikevoodoo.lssmp.elimination.EliminationInfo;
import me.ikevoodoo.lssmp.feature.heart.BasicElimination;
import me.ikevoodoo.lssmp.feature.heart.BasicHeartTake;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipeline;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerSelectionHandler implements HelixComponentHandler {

    private final Configuration generalSection;

    public PlayerSelectionHandler(Configuration generalSection) {
        this.generalSection = generalSection;
    }

    @Override
    public void render(HelixComponentContext context, HelixPageComponent component) {
        var players = component.<List<EliminationInfo>>getProperty("players", List.of());
        assert players != null;

        for (int x = 0; x < component.dimensions().width(); x++) {
            for (int y = 0; y < component.dimensions().height(); y++) {
                var pos = component.position(x, y);

                if (players.size() <= pos.slot()) {
                    continue;
                }

                var stack = new ItemStack(Material.PLAYER_HEAD);
                var meta = (SkullMeta) stack.getItemMeta();
                assert meta != null;

                meta.setLore(List.of("§7"));

                meta.setOwningPlayer(players.get(pos.slot()).player());
                stack.setItemMeta(meta);

                context.setItem(pos, stack);
            }
        }
    }

    @Override
    public void handleEvent(HelixComponentEvent event, HelixComponentContext context, HelixPageComponent component) {
        event.cancel();

        var position = event.clickPosition();

        var owning = this.getOwningPlayer(context.getItem(position));
        if (owning == null) {
            component.markDirty();
            return;
        }

        if(!this.revivePlayer(event.player(), owning)) return;

        var id = component.getProperty("id", "default_revive_beacon");
        assert id != null;

        Helix.items().removeFromPlayer(event.player(), UniqueIdentifier.combine(Constants.PLUGIN_KEY, id), 1);

        var hearts = component.getProperty("heart_cost", 0);
        this.removeHearts(event.player(), hearts == null ? 0 : hearts);

        event.player().closeInventory();

        component.markDirty();
    }

    @Override
    public void close(HelixComponentContext context, HelixPageComponent component) {
        // TODO add a feature to auto-reopen
    }

    private OfflinePlayer getOwningPlayer(ItemStack item) {
        if (item == null) return null;

        var meta = item.getItemMeta();
        if (!(meta instanceof SkullMeta skullMeta)) return null;

        return skullMeta.getOwningPlayer();
    }

    private void removeHearts(Player player, double hearts) {
        if (hearts == 0) return;

        var screenPipeline = HeartPipeline.create()
                .andThen(new BasicHeartTake(hearts))
                .andThen(new BasicElimination(this.generalSection));

        var attrib = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert attrib != null;

        var atomic = new AtomicDouble(attrib.getBaseValue());

        var cancel = new AtomicBoolean();

        screenPipeline.fire(player, null, atomic, null, cancel);

        if (!cancel.get()) {
            attrib.setBaseValue(atomic.get());
        }
    }

    private boolean revivePlayer(Player reviver, OfflinePlayer selected) {
        var tag = Helix.tags().get("elimination");

        if (!tag.has(selected.getUniqueId())) {
            reviver.sendMessage("§cI'm sorry, but that player is not eliminated!");
            return false;
        }

        tag.remove(selected.getUniqueId());

        reviver.playEffect(EntityEffect.TOTEM_RESURRECT);

        var subtitle = new MessageBuilder()
                .literal("Revived ")
                .color(MinecraftColor.GREEN)
                .bold(true)

                .literal(selected.getName())
                .color(MinecraftColor.RED)
                .bold(true)
                .build();
        reviver.sendTitle(" ", subtitle.toLegacyText(), 10, 20, 10);
        return true;
    }
}
