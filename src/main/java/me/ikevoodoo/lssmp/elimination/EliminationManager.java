package me.ikevoodoo.lssmp.elimination;

import me.ikevoodoo.helix.api.namespaced.UniqueIdentifier;
import me.ikevoodoo.helix.api.storage.HelixDataStorage;
import me.ikevoodoo.helix.api.tags.HelixTag;
import me.ikevoodoo.helix.api.tags.behaviors.HelixTagInstance;
import me.ikevoodoo.helix.api.tags.behaviors.TagBehaviors;
import me.ikevoodoo.helix.api.tags.behaviors.TagResult;
import me.ikevoodoo.helix.api.tags.behaviors.join.JoinTagContext;
import me.ikevoodoo.helix.api.tags.behaviors.join.async.AsyncJoinTagContext;
import me.ikevoodoo.helix.api.tags.behaviors.quit.QuitTagContext;
import me.ikevoodoo.helix.api.tags.behaviors.remove.RemoveTagContext;
import me.ikevoodoo.lssmp.configuration.data.eliminations.EliminationConfigurations;
import me.ikevoodoo.lssmp.elimination.context.EliminationContext;
import me.ikevoodoo.lssmp.elimination.context.SwitchMode;
import me.ikevoodoo.lssmp.elimination.modes.EliminationMode;
import me.ikevoodoo.lssmp.elimination.result.EliminationAsyncJoinResult;
import me.ikevoodoo.lssmp.elimination.result.EliminationResult;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.*;

public final class EliminationManager {

    private final HelixTag eliminationTag;
    private final EliminationConfigurations configurations;
    private final Map<String, EliminationMode> eliminationModes = new HashMap<>();
    private final Map<UUID, EliminationInfo> eliminatedPlayers = new HashMap<>();
    private final Collection<EliminationInfo> eliminatedPlayersView = Collections.unmodifiableCollection(this.eliminatedPlayers.values());
    private EliminationMode fallback;

    public EliminationManager(HelixTag eliminationTag, EliminationConfigurations configurations) {
        this.eliminationTag = eliminationTag;
        this.configurations = configurations;
    }

    public Collection<EliminationInfo> eliminatedPlayersView() {
        return this.eliminatedPlayersView;
    }

    public void initializeTagHandling(@NotNull String key) {
        this.eliminatedPlayers.clear();

        for (final var entry : this.eliminationTag.listAll()) {
            final var data = this.eliminationTag.getData(entry);
            if (data == null) {
                continue;
            }

            this.eliminatedPlayers.put(entry, EliminationInfo.fromStorage(entry, data));
        }

        this.eliminationTag.on(TagBehaviors.ASYNC_JOIN, UniqueIdentifier.combine(key, "async"), this::tagAsyncJoin);
        this.eliminationTag.on(TagBehaviors.JOIN, UniqueIdentifier.combine(key, "join"), this::tagJoin);
        this.eliminationTag.on(TagBehaviors.QUIT, UniqueIdentifier.combine(key, "quit"), this::tagQuit);
        this.eliminationTag.on(TagBehaviors.REMOVE, UniqueIdentifier.combine(key, "remove"), this::tagRemove);
    }

    public void register(EliminationMode eliminationMode, boolean fallback) {
        this.eliminationModes.put(eliminationMode.id(), eliminationMode);

        if (fallback) {
            this.fallback = eliminationMode;
        }
    }

    public EliminationResult tryEliminate(@NotNull Player victim, @Nullable Player killer) {
        final var victimId = victim.getUniqueId();
        if (this.eliminationTag.has(victimId)) {
            return this.fireAttemptElimination(victim, killer);
        }

        final var configuration = this.configurations.findHighestConfiguration(victim);
        final var eliminationInfo = new EliminationInfo(victim, killer, configuration);
        this.eliminationTag.add(victimId, eliminationInfo::initializePlayerData);
        this.eliminatedPlayers.put(victimId, eliminationInfo);

        final var eliminationMode = this.getEliminationMode(eliminationInfo);
        final var context = new EliminationContext(eliminationInfo);

        eliminationMode.onEliminated(victim, killer, context);

        this.handleSwitchTo(context);

        return EliminationResult.ELIMINATION;
    }

    public boolean tryRevive(@NotNull OfflinePlayer revived, @Nullable Player reviver) {
        final var revivedId = revived.getUniqueId();

        final var eliminationInfo = this.getEliminationInfo(revivedId);
        if (eliminationInfo == null) {
            return false;
        }

        final var eliminationMode = this.getEliminationMode(eliminationInfo);
        if (revived.isOnline()) {
            this.eliminationTag.remove(revivedId);
        } else {
            this.eliminationTag.editData(revivedId, eliminationInfo.configuration()::removeBanTime);
        }

        final var context = new EliminationContext(eliminationInfo);
        eliminationMode.onRevived(revived, reviver, context);

        this.handleSwitchTo(context);
        return true;
    }

    public EliminationAsyncJoinResult tryAsyncJoin(@NotNull UUID player, @NotNull InetAddress address) {
        final var info = this.getEliminationInfo(player);
        if (info == null) {
            return null;
        }

        final var eliminationMode = this.getEliminationMode(info);
        final var context = new EliminationContext(info);
        final var result = eliminationMode.onAsyncJoin(player, address, context);

        this.handleSwitchTo(context);
        return result;
    }

    public void tryJoin(@NotNull Player player) {
        final var info = this.getEliminationInfo(player.getUniqueId());
        if (info == null) {
            return;
        }

        final var eliminationMode = this.getEliminationMode(info);
        final var context = new EliminationContext(info);
        eliminationMode.onJoin(player, context);

        this.handleSwitchTo(context);
    }

    public void tryQuit(@NotNull Player player) {
        final var info = this.getEliminationInfo(player.getUniqueId());
        if (info == null) {
            return;
        }

        final var eliminationMode = this.getEliminationMode(info);
        final var context = new EliminationContext(info);
        eliminationMode.onQuit(player, context);

        this.handleSwitchTo(context);
    }

    private EliminationResult fireAttemptElimination(@NotNull Player victim, @Nullable Player killer) {
        final var victimId = victim.getUniqueId();

        final var eliminationInfo = this.getEliminationInfo(victimId);
        if (eliminationInfo == null) {
            return EliminationResult.NO_ACTION;
        }

        this.eliminationTag.editData(victimId, eliminationInfo::editPlayerData);

        final var eliminationMode = this.getEliminationMode(eliminationInfo);
        final var context = new EliminationContext(eliminationInfo);

        final var attemptSupported = eliminationMode.onAttemptElimination(victim, killer, context);
        if (!attemptSupported) {
            return EliminationResult.NO_ACTION;
        }

        this.handleSwitchTo(context);

        return EliminationResult.ATTEMPTED_ELIMINATION;
    }

    private void handleSwitchTo(EliminationContext context) {
        if (context.switchMode().type() != SwitchMode.Type.SWITCH_TO) {
            return;
        }

        final var info = context.info();
        final var victimId = info.victim().getUniqueId();
        this.eliminationTag.editData(victimId, data -> data.setString("eliminationMode", context.switchMode().id()));

        // Update the cache
        this.eliminatedPlayers.put(victimId, this.getEliminationInfo(victimId));
    }

    @Nullable
    private EliminationInfo getEliminationInfo(UUID playerId) {
        final var playerData = this.eliminationTag.getData(playerId);
        if (playerData == null) {
            return null;
        }

        return EliminationInfo.fromStorage(playerId, playerData);
    }

    @NotNull
    private EliminationMode getEliminationMode(EliminationInfo info) {
        return this.getEliminationMode(info.configuration().eliminationMode());
    }

    @NotNull
    private EliminationMode getEliminationMode(String id) {
        final var eliminationMode = this.eliminationModes.getOrDefault(id, this.fallback);
        if (eliminationMode == null) {
            throw new IllegalStateException();
        }

        return eliminationMode;
    }

    private TagResult tagAsyncJoin(AsyncJoinTagContext context, HelixDataStorage storage, HelixTagInstance instance) {
        final var result = this.tryAsyncJoin(context.player(), context.address());
        if (result == null) {
            return TagResult.SUCCESS;
        }

        if (result.removeTag()) {
            instance.remove();
            return TagResult.SUCCESS;
        }

        if (result.kickMessage() == null) {
            return TagResult.SUCCESS;
        }

        context.kick(result.kickMessage());
        return TagResult.FAILURE;
    }

    private TagResult tagJoin(JoinTagContext context, HelixDataStorage storage, HelixTagInstance instance) {
        this.tryJoin(context.player());
        return TagResult.SUCCESS;
    }

    private TagResult tagQuit(QuitTagContext context, HelixDataStorage storage, HelixTagInstance instance) {
        this.tryQuit(context.player());
        return TagResult.SUCCESS;
    }

    private TagResult tagRemove(RemoveTagContext context, HelixDataStorage storage, HelixTagInstance instance) {
        this.eliminatedPlayers.remove(context.target());
        return TagResult.FAILURE;
    }

}
