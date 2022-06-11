package me.ikevoodoo.lssmp;

import me.ikevoodoo.lssmp.bstats.Metrics;
import me.ikevoodoo.smpcore.SMPPlugin;

public final class LSSMP extends SMPPlugin {

    @Override
    public void onPreload() {
        saveResource("heartRecipe.yml", false);
        saveResource("beaconRecipe.yml", false);
        new Metrics(this, 12177);
    }
}