package me.ikevoodoo.lssmp;

import me.ikevoodoo.smpcore.SMPPlugin;

public final class LSSMP extends SMPPlugin {

    @Override
    public void onPreload() {
        saveResource("heartRecipe.yml", false);
        saveResource("beaconRecipe.yml", false);
    }
}