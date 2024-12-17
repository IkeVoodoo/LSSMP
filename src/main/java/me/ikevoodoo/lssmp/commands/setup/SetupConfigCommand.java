package me.ikevoodoo.lssmp.commands.setup;

import com.mojang.brigadier.arguments.StringArgumentType;
import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.helix.api.messages.MessageBuilder;
import me.ikevoodoo.helix.api.messages.colors.MinecraftColor;
import me.ikevoodoo.helix.api.plugins.HelixPlugin;
import me.ikevoodoo.lssmp.commands.setup.handlers.SetupBooleanHandler;
import me.ikevoodoo.lssmp.commands.setup.handlers.SetupChatHandler;
import me.ikevoodoo.lssmp.commands.setup.handlers.SetupCommandHandler;
import me.ikevoodoo.lssmp.commands.setup.handlers.SetupEnumHandler;
import me.ikevoodoo.lssmp.configuration.data.types.HeartLossMode;
import me.ikevoodoo.lssmp.configuration.data.types.PlayerDropHeartsMode;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import static me.ikevoodoo.helix.logging.LoggerColoring.chatColor;


public class SetupConfigCommand extends HelixCommand {

    private static final MinecraftColor MAIN_COLOR = MinecraftColor.fromHex("#B0B0B0");
    private static final MinecraftColor DESC_COLOR = MinecraftColor.fromHex("#808080");
    private static final MinecraftColor RED_COLOR = MinecraftColor.fromHex("#FF5555");

    private final Map<String, SetupCommandHandler> handlers = new LinkedHashMap<>();
    private final Map<UUID, BiConsumer<Player, String>> commandNameGetters;

    private final Configuration commandConfig;

    public SetupConfigCommand(Map<UUID, BiConsumer<Player, String>> commandNameGetters, Configuration commandConfig, Configuration generalConfig) {
        this.commandNameGetters = commandNameGetters;
        this.commandConfig = commandConfig;

        this.handlers.put("init", new SetupCommandHandler() {
            @Override
            public String name() {
                return null;
            }

            @Override
            public String description() {
                return null;
            }

            @Override
            public String summary() {
                return null;
            }
        });

        this.handlers.put("withdraw_name", new SetupChatHandler() {

            @Override
            public boolean onSubmit(String name) {
                var cmd = commandConfig.child("withdrawCommand").value("name");
                cmd.value(name);
                return true;
            }

            @Override
            public String name() {
                return "/lswithdraw";
            }

            @Override
            public String description() {
                return "Allows people to withdraw their hearts into an item.";
            }

            @Override
            public String summary() {
                return new MessageBuilder()
                        .literal("Set")
                        .color(MinecraftColor.GRAY)

                        .literal(" /lswithdraw ")
                        .color(MinecraftColor.RED)

                        .literal("command name to")
                        .color(MinecraftColor.GRAY)

                        .literal(" /%s", commandConfig.child("withdrawCommand").value("name").value())
                        .color(MinecraftColor.GOLD)
                        .build()
                        .toLegacyText();
            }

        });

        this.handlers.put("withdraw", new SetupBooleanHandler() {
            @Override
            public String name() {
                return "/" + commandConfig.child("withdrawCommand").value("name").value();
            }

            @Override
            public String description() {
                return "Allows people to withdraw their hearts into an item.";
            }

            @Override
            public void onSubmit(boolean allow) {
                var cmd = commandConfig.child("withdrawCommand").value("permission");

                if (allow) {
                    cmd.value("");
                } else {
                    cmd.value("lssmp.withdraw");
                }
            }

            @Override
            public String summary() {
                return new MessageBuilder()
                        .literal("Set")
                        .color(MinecraftColor.GRAY)

                        .literal(" %s ", this.name())
                        .color(MinecraftColor.DARK_AQUA)

                        .literal("permission to")
                        .color(MinecraftColor.GRAY)

                        .literal(" \"%s\"", commandConfig.child("withdrawCommand").value("permission").value())
                        .color(MinecraftColor.GOLD)
                        .build()
                        .toLegacyText();
            }
        });

        this.handlers.put("withdraw_elimination", new SetupBooleanHandler() {
            @Override
            public String name() {
                return "/" + commandConfig.child("withdrawCommand").value("name").value();
            }

            @Override
            public String description() {
                return "Do you want people to be able to eliminate themselves with the %s%s %scommand?".formatted(
                        MinecraftColor.DARK_AQUA,
                        this.name(),
                        MAIN_COLOR
                );
            }

            @Override
            public void onSubmit(boolean allow) {
                commandConfig.child("withdrawCommand").value("allowSelfElimination").value(allow);
            }

            @Override
            public boolean isCommand() {
                return false;
            }

            @Override
            public String summary() {
                final var eliminateMode = commandConfig.child("withdrawCommand").<Boolean>value("allowSelfElimination").value();
                return new MessageBuilder()
                        .literal("Set")
                        .color(MinecraftColor.GRAY)

                        .literal(" %s ", this.name())
                        .color(MinecraftColor.DARK_AQUA)

                        .literal("elimination mode to")
                        .color(MinecraftColor.GRAY)

                        .literal(" %s", eliminateMode ? "ELIMINATE" : "DENY")
                        .color(eliminateMode ? MinecraftColor.GREEN : MinecraftColor.RED)
                        .build()
                        .toLegacyText();
            }
        });

        this.handlers.put("recipe_name", new SetupChatHandler() {

            @Override
            public boolean onSubmit(String name) {
                var cmd = commandConfig.child("recipeCommand").value("name");
                cmd.value(name);
                return true;
            }

            @Override
            public String name() {
                return "/lsrecipe";
            }

            @Override
            public String description() {
                return "Allows people to view recipes";
            }

            @Override
            public String summary() {
                return new MessageBuilder()
                        .literal("Set")
                        .color(MinecraftColor.GRAY)

                        .literal(" /lsrecipe ")
                        .color(MinecraftColor.RED)

                        .literal("command name to")
                        .color(MinecraftColor.GRAY)

                        .literal(" /%s", commandConfig.child("recipeCommand").value("name").value())
                        .color(MinecraftColor.GOLD)
                        .build()
                        .toLegacyText();
            }

        });

        this.handlers.put("recipe", new SetupBooleanHandler() {
            @Override
            public String name() {
                return "/" + commandConfig.child("recipeCommand").value("name").value();
            }

            @Override
            public String description() {
                return "Allows people to view recipes";
            }

            @Override
            public void onSubmit(boolean allow) {
                var cmd = commandConfig.child("recipeCommand").value("permission");

                if (allow) {
                    cmd.value("");
                } else {
                    cmd.value("lssmp.recipe");
                }
            }

            @Override
            public String summary() {
                return new MessageBuilder()
                        .literal("Set")
                        .color(MinecraftColor.GRAY)

                        .literal(" %s ", this.name())
                        .color(MinecraftColor.DARK_AQUA)

                        .literal("permission to")
                        .color(MinecraftColor.GRAY)

                        .literal(" \"%s\"", commandConfig.child("recipeCommand").value("permission").value())
                        .color(MinecraftColor.GOLD)
                        .build()
                        .toLegacyText();
            }
        });

        this.handlers.put("elimination_mode", new SetupEnumHandler<HeartLossMode>() {
            @Override
            public String name() {
                return "Heart Loss Mode";
            }

            @Override
            public String description() {
                return "Determines how and when people will lose a heart";
            }

            @Override
            public Class<HeartLossMode> getType() {
                return HeartLossMode.class;
            }

            @Override
            public String description(HeartLossMode value) {
                return MinecraftColor.DARK_AQUA + switch (value) {
                    case NEVER -> "Never allow players to lose hearts.";
                    case ALWAYS -> "Allow players to lose hearts from player and non-player kills.";
                    case PLAYERS_ONLY -> "Allow players to lose hearts only from player kills.";
                    case ENVIRONMENT_ONLY -> "Allow players to lose hearts only from non-player kills.";
                };
            }

            @Override
            public void onSubmit(HeartLossMode value) {
                var combat = generalConfig.child("combat");
                var loss = combat.child("heartLoss");
                loss.value("heartLossMode").value(value);
            }

            @Override
            public String summary() {
                return new MessageBuilder()
                        .literal("Set")
                        .color(MinecraftColor.GRAY)

                        .literal(" Heart Loss Mode ")
                        .color(MinecraftColor.DARK_AQUA)

                        .literal("to")
                        .color(MinecraftColor.GRAY)

                        .literal(" %s", generalConfig.child("combat").child("heartLoss").value("heartLossMode").value())
                        .color(MinecraftColor.GOLD)
                        .build()
                        .toLegacyText();
            }
        });

        this.handlers.put("drop_hearts", new SetupBooleanHandler() {
            @Override
            public void onSubmit(boolean allow) {
                var combat = generalConfig.child("combat");
                if (!allow) {
                    combat.value("playerDropHeartsMode").value(PlayerDropHeartsMode.NEVER);
                    return;
                }

                var loss = combat.child("heartLoss");
                var mode = loss.<HeartLossMode>value("heartLossMode").value();

                var dropMode = switch (mode) {
                    case ALWAYS -> PlayerDropHeartsMode.ALWAYS;
                    case PLAYERS_ONLY -> PlayerDropHeartsMode.PLAYER_KILLS_ONLY;
                    case ENVIRONMENT_ONLY -> PlayerDropHeartsMode.ENVIRONMENT_KILLS_ONLY;
                    case NEVER -> PlayerDropHeartsMode.NEVER;
                };

                combat.value("playerDropHeartsMode").value(dropMode);
            }

            @Override
            public String name() {
                return null;
            }

            @Override
            public String description() {
                return "Should hearts always be dropped as an item instead of being instantly given to the killer?";
            }

            @Override
            public String summary() {
                return new MessageBuilder()
                        .literal("Set")
                        .color(MinecraftColor.GRAY)

                        .literal(" Heart Drop Mode ")
                        .color(MinecraftColor.DARK_AQUA)

                        .literal("to")
                        .color(MinecraftColor.GRAY)

                        .literal(" %s", generalConfig.child("combat").value("playerDropHeartsMode").value())
                        .color(MinecraftColor.GOLD)
                        .build()
                        .toLegacyText();
            }

            @Override
            public boolean isCommand() {
                return false;
            }
        });

        this.handlers.put("default_hearts", new SetupChatHandler() {
            @Override
            public boolean onSubmit(String chat) {
                try {
                    var value = Double.parseDouble(chat);
                    generalConfig.child("general").value("defaultHearts").value(value);

                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            @Override
            public String name() {
                return null;
            }

            @Override
            public String description() {
                return "How many hearts should players have by default? " + DESC_COLOR + "(Needs to be a number between 1 and 1024)";
            }

            @Override
            public boolean isCommand() {
                return false;
            }

            @Override
            public String summary() {
                final var defaultHearts = (double) generalConfig.child("general").value("defaultHearts").value();
                return new MessageBuilder()
                        .literal("Set")
                        .color(MinecraftColor.GRAY)

                        .literal(" Default Hearts ")
                        .color(MinecraftColor.DARK_AQUA)

                        .literal("to")
                        .color(MinecraftColor.GRAY)

                        .literal(" %s",defaultHearts / 2D)
                        .color(MinecraftColor.GOLD)
                        .build()
                        .toLegacyText();
            }
        });

        this.handlers.put("complete", new SetupCommandHandler() {
            @Override
            public String name() {
                return null;
            }

            @Override
            public String description() {
                return null;
            }

            @Override
            public String summary() {
                return null;
            }

            @Override
            public void onSubmit(Player player) {
                SetupConfigCommand.this.commandConfig.save();
                generalConfig.save();

                for (var handler : handlers.values()) {
                    var summary = handler.summary();
                    if (summary == null) continue;

                    player.sendMessage(summary);
                }

                player.sendMessage("§aPermission config finished! Saved, reloading.");
                try {
                    Helix.pluginLoader().reload(HelixPlugin.getProvidingPlugin(SetupConfigCommand.class));
                } catch (URISyntaxException | InvalidPluginException | InvalidDescriptionException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create("config")
                .argument("mode", StringArgumentType.greedyString());
    }

    @Override
    public CommandExecutionResult handlePlayerSender(@NotNull Player player, @NotNull ArgumentList args) {
        var modeArg = args.<String>getArgument("mode");

        player.sendMessage("\n§r".repeat(20));

        if (modeArg.startsWith("ERROR!")) {
            var slash = modeArg.indexOf('/');

            var sub = modeArg.substring("ERROR!".length(), slash);
            player.sendMessage(chatColor(sub));

            modeArg = modeArg.substring(slash);
        }

        var mode = modeArg;

        var parts = mode.split("/");
        var last = parts[parts.length - 1];

        var key = last.contains("-") ? last.split("-")[0] : last;

        var handler = this.handlers.get(key);
        if (handler == null) {
            player.sendMessage("§cUnable to setup those perms!");

            return CommandExecutionResult.HANDLED;
        }

        if (handler instanceof SetupEnumHandler<?> enumHandler) {
            var choosing = last.contains("-");

            if (!choosing) {
                player.spigot().sendMessage(this.getEnumMessage(enumHandler, mode));
                return CommandExecutionResult.HANDLED;
            }

            var choiceStr = last.substring(last.indexOf('-') + 1);
            
            try {
                this.submitEnumHandler(enumHandler, choiceStr.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                var curr = mode.substring(0, mode.indexOf('-'));
                player.chat("/lssetup config ERROR!<light:red>Invalid value <light:cyan>" + choiceStr + curr);
                return CommandExecutionResult.HANDLED;
            }
        }

        if (handler instanceof SetupChatHandler name) {
            player.spigot().sendMessage(this.getNameMessage(name));
            this.commandNameGetters.put(player.getUniqueId(), (plr, string) -> Helix.scheduler().sync(() -> {
                var next = getNext(mode, key);
                if(!name.onSubmit(string)) {
                    plr.chat("/lssetup config ERROR!<light:red>Invalid value <light:cyan>" + string + mode);
                    return;
                }

                plr.chat("/lssetup config " + next);
            }));

            return CommandExecutionResult.HANDLED;
        }

        var isPerms = handler instanceof SetupBooleanHandler;

        if (isPerms) {
            var confirm = last.contains("-");
            var allow = last.endsWith("-allow");

            var perms = (SetupBooleanHandler) handler;

            if (!confirm) {
                player.spigot().sendMessage(this.getPermsMessage(perms, mode));
                return CommandExecutionResult.HANDLED;
            }

            perms.onSubmit(allow);
        } else {
            handler.onSubmit(player);
        }

        var next = getNext(mode, key);
        if (next != null) {
            player.chat("/lssetup config " + next);
        }

        return CommandExecutionResult.HANDLED;
    }

    private <T extends Enum<T>> void submitEnumHandler(SetupEnumHandler<T> handler, String name) {
        handler.onSubmit(Enum.valueOf(handler.getType(), name));
    }

    private String getNext(String path, String currentKey) {
        boolean foundCurrent = false;
        for (var key : this.handlers.keySet()) {
            if (foundCurrent) {
                return path + "/" + key;
            }

            if (key.equals(currentKey)) {
                foundCurrent = true;
            }
        }

        return null;
    }

    private <T extends Enum<T>> BaseComponent[] getEnumMessage(SetupEnumHandler<T> handler, String path) {
        var main = MinecraftColor.fromHex("#B0B0B0");
        var desc = MinecraftColor.fromHex("#808080");

        var builder = new MessageBuilder()
                .literal("What should the ")
                .color(main)

                .literal(handler.name())
                .color(MinecraftColor.DARK_AQUA)

                .literal(" option be set to?\n")
                .color(main)

                .literal("→ ")
                .color(main)

                .literal(handler.description())
                .color(desc)

                .literal("\n§m%s\n".formatted(" ".repeat(65)))
                .color(main);

        var values = handler.getType().getEnumConstants();

        for (int i = 0; i < values.length; i++) {
            var val = values[i];
            if (i != 0 && i % 3 == 0) {
                builder.literal("\n");
            }
            
            var des = handler.description(val);

            builder
                    .literal("[ %s ] ", val)
                    .color(MinecraftColor.GOLD)

                    .click(ClickEvent.Action.RUN_COMMAND, "/lssetup config " + path + "-" + val)
                    .hover(HoverEvent.Action.SHOW_TEXT, new Text(des == null ? MinecraftColor.GOLD + "Select" : des));
        }

        return builder.buildArray();
    }

    private BaseComponent[] getNameMessage(SetupChatHandler handler) {
        var builder = new MessageBuilder();

        if (handler.isCommand()) {
                builder.literal("What do you want the ")
                    .color(MAIN_COLOR)

                    .literal(handler.name())
                    .color(MinecraftColor.DARK_AQUA)

                    .literal(" command to be named?\n")
                    .color(MAIN_COLOR)

                    .literal("→ ")
                    .color(MAIN_COLOR);
        }

        return builder.literal(handler.description())
                .color(handler.isCommand() ? DESC_COLOR : MAIN_COLOR)

                .literal("\n§m%s\n".formatted(" ".repeat(65)))
                .color(MAIN_COLOR)

                .literal(handler.isCommand() ? "Type the name in chat, without the /" : "Type the value in chat")
                .color(MinecraftColor.GOLD)
                .bold(true)
                .buildArray();
    }

    private BaseComponent[] getPermsMessage(SetupBooleanHandler handler, String path) {
        MessageBuilder builder = new MessageBuilder();
        if (handler.isCommand()) {
            builder.literal("Do you wish for people to be able to use the ")
                    .color(MAIN_COLOR)

                    .literal(handler.name())
                    .color(MinecraftColor.DARK_AQUA)

                    .literal(" command?\n")
                    .color(MAIN_COLOR)

                    .literal("→ ")
                    .color(MAIN_COLOR);
        }

        return builder
                .literal(handler.description())
                .color(handler.isCommand() ? DESC_COLOR : MAIN_COLOR)

                .literal("\n§m%s\n".formatted(" ".repeat(65)))
                .color(MAIN_COLOR)


                .literal("[ YES ]")
                .color(MinecraftColor.GOLD)
                .bold(true)
                .underlined(true)
                .click(ClickEvent.Action.RUN_COMMAND, "/lssetup config %s-allow".formatted(path))
                .hover(HoverEvent.Action.SHOW_TEXT, new Text(MinecraftColor.GOLD + "Allow the option"))

                .literal("   |   ")
                .color(MAIN_COLOR)

                .literal("[ NO ]")
                .color(RED_COLOR)
                .bold(true)
                .click(ClickEvent.Action.RUN_COMMAND, "/lssetup config %s-deny".formatted(path))
                .hover(HoverEvent.Action.SHOW_TEXT, new Text(MinecraftColor.RED + "Deny the option"))
                .buildArray();
    }
}
