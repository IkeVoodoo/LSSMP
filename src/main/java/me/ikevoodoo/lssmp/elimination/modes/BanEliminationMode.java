package me.ikevoodoo.lssmp.elimination.modes;

import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.lssmp.configuration.data.types.EliminationNotificationMode;
import me.ikevoodoo.lssmp.elimination.context.EliminationContext;
import me.ikevoodoo.lssmp.elimination.result.EliminationAsyncJoinResult;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.UUID;

public class BanEliminationMode implements EliminationMode {

    private final Configuration generalSection;

    public BanEliminationMode(Configuration generalSection) {
        this.generalSection = generalSection;
    }

    @Override
    public String id() {
        return "ban_player";
    }

    @Override
    public boolean onAttemptElimination(@NotNull Player victim, @Nullable Player killer, @NotNull EliminationContext ctx) {
        // Do nothing. Not supported.
        return false;
    }

    @Override
    public void onEliminated(@NotNull Player victim, @Nullable Player killer, @NotNull EliminationContext ctx) {
        victim.setFallDistance(0); // This prevents infinite death

        final var info = ctx.info();
        victim.kickPlayer(info.getKickMessage(victim));

        final var notificationMode = info.configuration().notificationMode();
        if (notificationMode == EliminationNotificationMode.SUPPRESS) {
            return;
        }

        final var notificationMessage = info.configuration().notificationMessage();
        final var formattedNotification = info.formatMessage(notificationMessage);

        switch (notificationMode) {
            case SEND_TO_KILLER -> {
                if (killer == null) {
                    return;
                }

                killer.sendMessage(formattedNotification);
            }

            case SEND_TO_EVERYONE -> Bukkit.broadcastMessage(formattedNotification);
        }
    }

    @Override
    public void onRevived(@NotNull OfflinePlayer revived, @Nullable Player reviver, @NotNull EliminationContext ctx) {
        // Do nothing.
    }

    @Override
    public EliminationAsyncJoinResult onAsyncJoin(@NotNull UUID eliminatedPlayer, @NotNull InetAddress address, @NotNull EliminationContext ctx) {
        final var now = System.currentTimeMillis();
        final var info = ctx.info();
        if (now < info.getPardonAt()) {
            return EliminationAsyncJoinResult.kick(info.getKickMessage(address));
        }

        return EliminationAsyncJoinResult.revive();
    }

    @Override
    public void onJoin(@NotNull Player player, @NotNull EliminationContext ctx) {
        final var now = System.currentTimeMillis();
        final var info = ctx.info();
        if (now < info.getPardonAt()) {
            return; // Could happen
        }

        final var configuration = info.configuration();

        final var reviveHearts = switch (configuration.reviveHeartsMode()) {
            case USE_REVIVE_HEARTS -> configuration.reviveHearts() * 2;
            case USE_DEFAULT_HEARTS -> this.generalSection.<Double>getValue("defaultHearts") * 2D;
        };

        player.setFallDistance(0); // Making sure
        final var maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealth != null;

        maxHealth.setBaseValue(reviveHearts);
        player.setHealthScale(reviveHearts);
    }

    @Override
    public void onQuit(@NotNull Player eliminatedPlayer, @NotNull EliminationContext ctx) {
        // Do nothing.
    }


}
