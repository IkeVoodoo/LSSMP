package me.ikevoodoo.lssmp.commands.eliminate;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.lssmp.config.MainConfig;
import me.ikevoodoo.lssmp.utils.Util;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class EliminateCommand extends SMPCommand {
    public EliminateCommand(SMPPlugin plugin) {
        super(plugin, plugin.getConfigHandler().extractValues(CommandConfig.class, commandConfig -> Map.of(
                "name", commandConfig.getEliminateCommand().name(),
                "permission", commandConfig.getEliminateCommand().perms()
        )));
        registerSubCommands(new EliminateAllCommand(plugin));
    }

    @Override
    public boolean execute(Context<?> context) {
        if(context.args().isEmpty()) {
            var cfg = getConfig(MainConfig.class).getMessages().getErrorMessages();
            context.source().sendMessage(cfg.specifyAtLeastOne().replace("%s", "player"));
            return true;
        }

        List<Player> players = context.args().getAll(Player.class);


        for(Player player : players) {
            Util.eliminate(getPlugin(), player);
        }


        context.source().sendMessage(getPlugin()
                .getConfigHandler()
                .getConfig(CommandConfig.class)
                .getEliminateCommand()
                .getMessages()
                .eliminatedPlayers().replace("%s", String.valueOf(players.size())));
        return true;
    }

}
