package me.ikevoodoo.lssmp.commands.give;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.OptionalFor;
import me.ikevoodoo.smpcore.items.CustomItem;
import org.bukkit.entity.Player;

import java.util.Map;

public class GiveCommand extends SMPCommand {
    public GiveCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getGiveCommand().name(),
                "permission", commandConfig.getGiveCommand().perms()
        )));
        setArgs(
                new Argument("item", true, String.class, OptionalFor.NONE,
                        context -> getPlugin()
                                .getItems()
                                .stream()
                                .filter(customItem -> customItem.getRecipeData() != null)
                                .map(CustomItem::getId)
                                .toList(),
                        true),
                new Argument("count", false, Integer.class, OptionalFor.ALL),
                new Argument("player", false, Player.class, OptionalFor.PLAYER)
        );

    }

    @Override
    public boolean execute(Context<?> context) {
        var cfg = getConfig(MainConfig.class).getMessages().getErrorMessages();
        getPlugin().getItem(context.args().get("item", String.class)).ifPresentOrElse(customItem -> {
            var target = context.args().get("player", Player.class, null);
            if (target == null) {
                if (context.source() instanceof Player player) target = player;
                else {
                    context.source().sendMessage(cfg.requiresPlayer());
                    return;
                }
            }
            int count = context.args().get("count", Integer.class, 1);

            target.getInventory().addItem(customItem.getItemStack(count));
        }, () -> context.source().sendMessage(String.format(cfg.requiresArgument(), "item")));
        return true;
    }


}
