package me.ikevoodoo.lssmp.screens;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.helix.api.screens.HelixScreen;
import me.ikevoodoo.helix.api.screens.ScreenDimensions;
import me.ikevoodoo.helix.api.screens.components.HelixPropertyCallback;
import me.ikevoodoo.helix.api.screens.setup.HelixScreenSetup;
import me.ikevoodoo.lssmp.elimination.EliminationInfo;
import me.ikevoodoo.lssmp.screens.components.PlayerSelectionHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ReviveScreen implements HelixScreen {

    private final Configuration generalConfig;
    private final Collection<EliminationInfo> players;
    private HelixPropertyCallback<Collection<EliminationInfo>> playerCallback;

    public ReviveScreen(Configuration generalConfig, Collection<EliminationInfo> players) {
        this.generalConfig = generalConfig;
        this.players = players;
    }

    @Override
    public void setup(HelixScreenSetup setup) {
        var page = setup.createPage("revive_page", "Choose a player to revive.", ScreenDimensions.chest(6));
        page.setBackground(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

        var component = page.addComponent("selection", new PlayerSelectionHandler(this.generalConfig));
        assert component != null;

        component.dimensions(ScreenDimensions.chest(5));

        this.playerCallback = component.setProperty("players", this.players);

        Helix.scheduler().timer(() -> this.playerCallback.fireUpdate(this.players), 2, TimeUnit.SECONDS);
    }

}
