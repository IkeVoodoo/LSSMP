package me.ikevoodoo.lssmp.commands.reset;

import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.config.Configuration;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public abstract class ResetCommandBase extends HelixCommand {

    protected void reset(Player player, Configuration generalConfig) {
        var def = generalConfig.<Double>getValue("defaultHearts");
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(def);
    }

}
