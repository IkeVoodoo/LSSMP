package me.ikevoodoo.lssmp;

import me.ikevoodoo.helix.api.namespaced.UniqueIdentifier;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

// THIS CLASS IS ONLY HERE TO WORK AROUND SOME SHORT-COMINGS IN HELIX
// IT WILL BE REMOVED AS SOON AS THEY ARE FIXED
// SEE HELIX REPO PROJECTS FOR MORE INFO
public class Constants {

    public static final String PLUGIN_KEY = JavaPlugin.getPlugin(LifestealInit.class).getName().toLowerCase(Locale.ROOT);

    public static final UniqueIdentifier REVIVE_SCREEN_ID = UniqueIdentifier.combine(PLUGIN_KEY, "revive_screen");
    public static final UniqueIdentifier RECIPE_SCREEN_ID = UniqueIdentifier.combine(PLUGIN_KEY, "recipe_screen");
}
