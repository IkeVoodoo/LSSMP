package me.ikevoodoo.lssmp.commands.perms;

import me.ikevoodoo.lssmp.config.CommandConfig;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.CommandUsable;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PermSetupCommand extends SMPCommand {

    private static final int ADDED_COMMAND = 0;
    private static final int UNKNOWN_COMMAND = 1;
    private static final int ALREADY_MARKED_COMMAND = 2;
    private static final int NONE = -1;

    private static final Map<String, String> ALLOWED_COMMANDS = new HashMap<>();

    static {
        ALLOWED_COMMANDS.put(CommandConfig.WithdrawCommand.name, CommandConfig.WithdrawCommand.perms);
        ALLOWED_COMMANDS.put(CommandConfig.RecipeCommand.name, CommandConfig.RecipeCommand.perms);
    }

    public PermSetupCommand(SMPPlugin plugin) {
        super(plugin, CommandConfig.PermSetupCommand.name, CommandConfig.PermSetupCommand.perms);
        setUsable(CommandUsable.PLAYER);
    }

    @Override
    public boolean execute(Context<?> context) {
        var player = context.source(Player.class);
        var allowed = new ArrayList<String>();

        var lp = Bukkit.getPluginManager().getPlugin("LuckPerms");
        if (lp == null) {
            player.sendMessage("§cMissing required dependency §bLuckPerms§c!");
            player.sendMessage("§cPlease install it from §ahttps://luckperms.net");
            return true;
        }

        getPlugin().getChatInputHandler().onCancellableInput(player, line -> {
            line = line.toLowerCase(Locale.ROOT).trim();

            if (line.equalsIgnoreCase("confirm")) {
                if (allowed.isEmpty()) {
                    player.sendMessage("§cNo perms to confirm!");
                    return true;
                }

                player.sendMessage("§aConfirming for the following commands:");
                for (var cmd : allowed) {
                    var perm = ALLOWED_COMMANDS.get(cmd);

                    player.sendMessage("§a - §f/" + cmd + " §7[§6" + perm + "§7]");

                    Bukkit.getScheduler().runTask(getPlugin(), () ->
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp group default permission set " + perm));
                }
                return true;
            }

            var code = UNKNOWN_COMMAND;
            
            if (allowed.contains(line)) {
                code = ALREADY_MARKED_COMMAND;
            } else if (ALLOWED_COMMANDS.containsKey(line)) {
                code = ADDED_COMMAND;
                allowed.add(line);
            }

            sendMenu(player, allowed, code, line);

            return false;
        }, "§cType \"§ccancel§c\" to cancel.", "§cCancelled permission setup!");

        sendMenu(player, allowed, NONE, null);
        return true;
    }

    private void sendMenu(Player player, List<String> allowed, int code, String data) {
        for (int i = 0; i < 150; i++) {
            player.sendMessage("§r");
        }
        player.sendMessage("§a===== LS PERM SETUP =====");
        switch (code) {
            case UNKNOWN_COMMAND -> player.sendMessage("§cUnknown command §f" + data);
            case ALREADY_MARKED_COMMAND -> player.sendMessage("§cAlready added command §f" + data);
            case ADDED_COMMAND -> player.sendMessage("§aAdded command §f" + data);
        }

        for (int i = 0; i < 5; i++) {
            player.sendMessage("§r");
        }

        sendCommands(player, allowed);
    }

    private void sendCommands(Player player, List<String> allowed) {
        if (allowed.size() == ALLOWED_COMMANDS.size()) {
            player.sendMessage("§aPlease type \"§6confirm§a\" to confirm!");
            return;
        }

        player.sendMessage("§aChoose one of the following commands or type \"§6confirm§a\" to confirm!");
        for (var cmd : ALLOWED_COMMANDS.keySet()) {

            var beforeComponent = new TextComponent(" - ");
            beforeComponent.setColor(ChatColor.GREEN);

            var cmdComponent = new TextComponent(cmd);
            cmdComponent.setColor(ChatColor.WHITE);

            if (!allowed.contains(cmd)) {
                cmdComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));
                cmdComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                        TextComponent.fromLegacyText("§aClick to type in chat box!")
                )));
            } else {
                cmdComponent.setStrikethrough(true);
                cmdComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                        TextComponent.fromLegacyText("§cAlready inserted!")
                )));
            }

            beforeComponent.addExtra(cmdComponent);

            player.spigot().sendMessage(beforeComponent);
        }
    }
}
