package me.ikevoodoo.lssmp;

import me.ikevoodoo.helix.BukkitHelixProvider;
import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.helix.api.items.display.ItemDisplayData;
import me.ikevoodoo.helix.api.items.display.ItemTextDisplayData;
import me.ikevoodoo.helix.api.logging.HelixLogger;
import me.ikevoodoo.helix.api.namespaced.UniqueIdentifier;
import me.ikevoodoo.helix.api.tags.behaviors.TagBehaviors;
import me.ikevoodoo.helix.api.tags.behaviors.TagResult;
import me.ikevoodoo.lssmp.commands.eliminate.EliminateCommand;
import me.ikevoodoo.lssmp.commands.health.HealthCommand;
import me.ikevoodoo.lssmp.commands.recipe.RecipeCommand;
import me.ikevoodoo.lssmp.commands.reset.ResetCommand;
import me.ikevoodoo.lssmp.commands.revive.ReviveCommand;
import me.ikevoodoo.lssmp.commands.setup.SetupCommand;
import me.ikevoodoo.lssmp.commands.withdraw.WithdrawCommand;
import me.ikevoodoo.lssmp.configuration.ConfigurationConverter;
import me.ikevoodoo.lssmp.configuration.data.eliminations.EliminationConfiguration;
import me.ikevoodoo.lssmp.configuration.data.items.custom.HeartItemConfiguration;
import me.ikevoodoo.lssmp.configuration.data.items.custom.ReviveBeaconConfiguration;
import me.ikevoodoo.lssmp.configuration.data.items.custom.messages.HeartItemMessages;
import me.ikevoodoo.lssmp.configuration.data.recipes.RecipeConfiguration;
import me.ikevoodoo.lssmp.configuration.data.types.*;
import me.ikevoodoo.lssmp.configuration.parsers.eliminations.EliminationConfigurationParser;
import me.ikevoodoo.lssmp.configuration.parsers.items.custom.HeartItemParser;
import me.ikevoodoo.lssmp.configuration.parsers.items.custom.ReviveBeaconParser;
import me.ikevoodoo.lssmp.elimination.EliminationHelper;
import me.ikevoodoo.lssmp.elimination.EliminationInfo;
import me.ikevoodoo.lssmp.feature.heart.*;
import me.ikevoodoo.lssmp.items.BaconItem;
import me.ikevoodoo.lssmp.items.HeartItem;
import me.ikevoodoo.lssmp.items.ReviveBeaconItem;
import me.ikevoodoo.lssmp.listeners.FirstJoinListener;
import me.ikevoodoo.lssmp.listeners.LifestealCombatListener;
import me.ikevoodoo.lssmp.listeners.PlayerChatListener;
import me.ikevoodoo.lssmp.pipeline.heart.HeartPipeline;
import me.ikevoodoo.lssmp.screens.RecipeScreen;
import me.ikevoodoo.lssmp.screens.ReviveScreen;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.RecipeChoice;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

import static me.ikevoodoo.lssmp.Constants.RECIPE_SCREEN_ID;
import static me.ikevoodoo.lssmp.Constants.REVIVE_SCREEN_ID;

public class Lifesteal {

    private final Configuration mainConfiguration;
    private final Configuration heartConfiguration;
    private final Configuration beaconConfiguration;
    private final Configuration eliminationConfiguration;
    private final Configuration commandConfiguration;
    private final List<EliminationInfo> eliminatedList = new ArrayList<>();
    private final Map<UUID, BiConsumer<Player, String>> messageConsumer = new HashMap<>();

    public Lifesteal(LifestealInit init) {
        this.mainConfiguration = Helix.config().createBuilder()
                .child("combat")
                .comment("=======================================================")
                .comment("All settings related to combat.")
                .comment("Note: ENVIRONMENT means non-player things, such as mobs")
                .comment("=======================================================")

                .value("playerDropHeartsMode", PlayerDropHeartsMode.MAX_HEARTS_ONLY)
                .comment("Should players drop hearts as items when they are killed?")
                .comment("Note: MAX_HEARTS_ONLY will only drop hearts if the killer is at max hearts.")
                .comment("Note: If based on the drop mode a player should drop hearts, it won't be instantly given to the killer!")
                .commentSpace()
                .commentEnum(PlayerDropHeartsMode.class)
                .next()

                .value("droppedHeartsFloat", true)
                .comment("Should dropped hearts float in the air, or fall to the ground?")
                .comment("Note: Only works when playerDropHeartsMode is not set to NEVER")
                .commentSpace()
                .next()

                .value("heartToDrop", "default_heart_item")
                .comment("What heart item should be used for heart drops?")
                .comment("Note: Only works when a heart should drop! See <playerDropHeartsMode> for more information!")
                .next()

                .child("heartLoss")
                .comment("Specifies heart-loss related functionality.")
                .comment("1.0 means one heart, 0.5 means half a heart, 10 hearts is one row of hearts.")

                .value("heartLossMode", HeartLossMode.PLAYERS_ONLY)
                .comment("Under what condition should players lose hearts?")
                .commentSpace()
                .commentEnum(HeartLossMode.class)
                .next()

                .value("environmentHeartLoss", 1.0D)
                .comment("How many hearts should environment kills take from a player?")
                .comment("Note: Only works when heartLossMode is set to ENVIRONMENT_ONLY or ALWAYS")
                .next()

                .value("playerHeartLoss", 1.0D)
                .comment("How many hearts should player kills take from a player?")
                .comment("Note: Only works when heartLossMode is set to PLAYER_ONLY or ALWAYS")
                .next()
                .parent()

                .value("totemUseMode", TotemUseMode.IN_HAND_ONLY)
                .comment("Should players be protected from losing a heart if they have a totem?")
                .comment("Note: IN_HAND_ONLY is the same as vanilla.")
                .comment("Note: ALWAYS means that a player will not lose a heart even if the totem is not in their hands.")
                .commentSpace()
                .commentEnum(TotemUseMode.class)
                .next()
                .parent()

                .child("general")
                .comment("Specifies heart related functionality.")
                .comment("1.0 means one heart, 0.5 means half a heart, 10 hearts is one row of hearts.")

                .value("easterEggs", false)
                .comment("Do you want to allow plugin easter eggs?")
                .next()

                .value("defaultHearts", 10.0D)
                .comment("The hearts of a player when they first join the server. Defaults to vanilla behavior.")
                .next()

                .value("minimumHearts", 0.0D)
                .comment("The minimum amount of hearts a player can reach.")
                .next()

                .value("maximumHearts", -1.0D)
                .comment("The maximum amount of hearts a player can reach.")
                .comment("Note: Use -1 to disable.")
                .next()

                .value("eliminatePlayers", true)
                .comment("Should players be eliminated when they reach the minimum amount of hearts?")
                .next()
                .parent()

                .build(new File(init.getDataFolder(), "config.yml"));

        this.heartConfiguration = Helix.config().createBuilder()
                .compoundArray("heartItems", new HeartItemConfiguration[] {
                    new HeartItemConfiguration(
                            new ItemDisplayData(
                                    Material.RED_DYE,
                                    933,
                                    new ItemTextDisplayData(
                                            "§c§lHeart Item",
                                            List.of("§r§6Right-Click §7to claim §6{{heart_count}} §7hearts!")
                                    )
                            ),
                            new RecipeConfiguration(
                                    true,
                                    new RecipeChoice[] {
                                            new RecipeChoice.MaterialChoice(Material.GOLD_INGOT),
                                            new RecipeChoice.MaterialChoice(Material.DIAMOND_BLOCK),
                                            new RecipeChoice.MaterialChoice(Material.GOLD_INGOT),

                                            new RecipeChoice.MaterialChoice(Material.DIAMOND_BLOCK),
                                            new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT),
                                            new RecipeChoice.MaterialChoice(Material.DIAMOND_BLOCK),

                                            new RecipeChoice.MaterialChoice(Material.GOLD_INGOT),
                                            new RecipeChoice.MaterialChoice(Material.DIAMOND_BLOCK),
                                            new RecipeChoice.MaterialChoice(Material.GOLD_INGOT)
                                    }
                            ),
                            1.0,
                            "default_heart_item",
                            new HeartItemMessages(
                                    "§aYou have claimed §6{{heart_count}} §ahearts!",
                                    "§cYou can't claim any more hearts!",
                                    "§e§lWARNING! §fYou can only claim §6{{available_hearts}} §fout of §6{{heart_count}} §fpossible hearts. §6Shift-Right-Click §f to confirm."
                            ),
                            true,
                            true,
                            -1,
                            true
                    )
                }, new HeartItemParser())
                .next()

                .build(new File(init.getDataFolder(), "heart.yml"));

        this.beaconConfiguration = Helix.config().createBuilder()
                .compoundArray("beaconItems", new ReviveBeaconConfiguration[] {
                        new ReviveBeaconConfiguration(
                                new ItemDisplayData(
                                        Material.BEACON,
                                        932,
                                        new ItemTextDisplayData(
                                                "§6§lRevive Beacon",
                                                List.of(
                                                        //"§r§6Right-Click §7to revive up to §6{{max_revives}} §7people!",
                                                        "§7This will §ccost §6{{heart_cost}} §cheart(s)"
                                                )
                                        )
                                ),
                                new RecipeConfiguration(
                                        true,
                                        new RecipeChoice[] {
                                                new RecipeChoice.MaterialChoice(Material.DANDELION),
                                                new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT),
                                                new RecipeChoice.MaterialChoice(Material.DANDELION),

                                                new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT),
                                                new RecipeChoice.MaterialChoice(Material.BEACON),
                                                new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT),

                                                new RecipeChoice.MaterialChoice(Material.DANDELION),
                                                new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT),
                                                new RecipeChoice.MaterialChoice(Material.DANDELION)
                                        }
                                ),
                                "default_revive_beacon",
                                1,
                                0,
                                true,
                                true
                        )
                }, new ReviveBeaconParser())
                .next()

                .build(new File(init.getDataFolder(), "beacon.yml"));

        this.eliminationConfiguration = Helix.config().createBuilder()
                .compoundArray("eliminations", new EliminationConfiguration[] {
                        new EliminationConfiguration(
                                "§cYou have been eliminated! Remaining time: §3{{time_remaining}}\n§cYou will be revived at §3{{revived_at}}",

                                EliminationNotificationMode.SEND_TO_EVERYONE,
                                "§c{{player}} was eliminated!",

                                "NONE",

                                -1,

                                ReviveHeartsMode.USE_DEFAULT_HEARTS,
                                10D,

                                true,

                                new String[] {
                                        "your",
                                        "commands",
                                        "here"
                                },

                                new String[] {
                                        "your",
                                        "commands",
                                        "here"
                                }
                        )
                }, new EliminationConfigurationParser())
                .next()

                .build(new File(init.getDataFolder(), "eliminations.yml"));

        this.commandConfiguration = Helix.config().createBuilder()
                .child("resetCommand")
                .comment("The config section for the /lsreset command.")
                .comment("WARNING! This is an admin command!")

                .value("name", "lsreset")
                .comment("The name of the command in-game, change this to change how it looks.")
                .next()

                .value("permission", "lssmp.reset")
                .comment("The permission of the command")
                .next()
                .parent()

                .child("reviveCommand")
                .comment("The config section for the /lsrevive command.")
                .comment("WARNING! This is an admin command!")

                .value("name", "lsrevive")
                .comment("The name of the command in-game, change this to change how it looks.")
                .next()

                .value("permission", "lssmp.revive")
                .comment("The permission of the command")
                .next()
                .parent()


                .child("eliminateCommand")
                .comment("The config section for the /lseliminate command.")
                .comment("WARNING! This is an admin command!")

                .value("name", "lseliminate")
                .comment("The name of the command in-game, change this to change how it looks.")
                .next()

                .value("permission", "lssmp.eliminate")
                .comment("The permission of the command")
                .next()
                .parent()


                .child("healthCommand")
                .comment("The config section for the /lshealth command.")
                .comment("WARNING! This is an admin command!")

                .value("name", "lshealth")
                .comment("The name of the command in-game, change this to change how it looks.")
                .next()

                .value("permission", "lssmp.health")
                .comment("The permission of the command")
                .next()
                .parent()

                .child("recipeCommand")
                .comment("The config section for the /lsrecipe command.")

                .value("name", "lsrecipe")
                .comment("The name of the command in-game, change this to change how it looks.")
                .next()

                .value("permission", "lssmp.recipe")
                .comment("The permission of the command")
                .next()
                .parent()

                .child("withdrawCommand")
                .comment("The config section for the /lswithdraw command.")

                .value("name", "lswithdraw")
                .comment("The name of the command in-game, change this to change how it looks.")
                .next()

                .value("permission", "lssmp.withdraw")
                .comment("The permission of the command")
                .next()

                .value("allowSelfElimination", true)
                .comment("Should people be able to eliminate themselves when withdrawing too much?")
                .next()

                .value("heartItem", "default_heart_item")
                .comment("What item should be given to the player that ran the command?")
                .next()

                .parent()

                .build(new File(init.getDataFolder(), "commands.yml"));
    }

    public void onEnable(LifestealInit init) {
        this.convertOldConfigs();

        this.reloadConfig(init);

        // UNUSED: I do not need custom charts for now
        final var metrics = new Metrics(init, 12177);

        var combatSection = this.mainConfiguration.child("combat");
        var heartLossSection = combatSection.child("heartLoss");

        var generalSection = this.mainConfiguration.child("general");

        var pipeline = HeartPipeline.create()
                .andThen(new BasicTotemCheck(combatSection))
                .andThen(new BasicHeartDeny(heartLossSection, generalSection))
                .andThen(new BasicHeartDrop(combatSection, generalSection))
                .andThen(new BasicHeartGain(heartLossSection))
                .andThen(new BasicHeartLoss(heartLossSection))
                .andThen(new BasicHeartCap(generalSection))
                .andThen(new BasicElimination(generalSection));

        var registry = Helix.events();

        registry.register(new LifestealCombatListener(pipeline));
        registry.register(new FirstJoinListener(generalSection, new NamespacedKey(init, "first_join")));
        registry.register(new PlayerChatListener(this.messageConsumer));

        var commands = Helix.commands();

        commands.register(init, new ResetCommand(generalSection, this.commandConfiguration.child("resetCommand")));
        commands.register(init, new ReviveCommand(this.commandConfiguration.child("reviveCommand")));
        commands.register(init, new EliminateCommand(this.commandConfiguration.child("eliminateCommand")));
        commands.register(init, new HealthCommand(this.commandConfiguration.child("healthCommand")));
        commands.register(init, new RecipeCommand(this.commandConfiguration.child("recipeCommand")));
        commands.register(init, new SetupCommand(this.commandConfiguration, this.mainConfiguration, this.messageConsumer));

        var withdrawPipeline = HeartPipeline.create()
                        .andThen(new BasicHeartTake(2.0))
                        .andThen(new BasicElimination(generalSection));
        commands.register(init, new WithdrawCommand(this.commandConfiguration.child("withdrawCommand"), withdrawPipeline));

        var screens = Helix.screens();
        if(!screens.register(REVIVE_SCREEN_ID, new ReviveScreen(generalSection, this.eliminatedList))) {
            HelixLogger.error("Unable to register revive_screen as it already exists!");
        }

        var tag = Helix.tags().get("elimination");
        this.eliminatedList.clear();
        for (var entry : tag.listAll()) {
            var storage = tag.getData(entry);
            if (!storage.has("playerMessage")) continue;

            var info = EliminationInfo.fromStorage(entry, storage);
            this.eliminatedList.add(info);
        }

        tag.on(TagBehaviors.ASYNC_JOIN, UniqueIdentifier.combine(init.getName().toLowerCase(Locale.ROOT), "kick_player"), (context, storage, instance) -> {
            var player = context.player();
            var data = EliminationInfo.fromStorage(player, storage);
            if (!data.configuration().shouldBanPlayer()) {
                return TagResult.SUCCESS;
            }

            var now = System.currentTimeMillis();
            var pardonAt = data.getPardonAt();

            if (now < pardonAt) {
                context.kick(data.getKickMessage(context.address()));
                return TagResult.FAILURE;
            }

            return TagResult.SUCCESS;
        });

        tag.on(TagBehaviors.JOIN, UniqueIdentifier.combine(init.getName().toLowerCase(Locale.ROOT), "clear_tag"), (context, storage, instance) -> {
            var player = context.player();
            var data = EliminationInfo.fromStorage(player.getUniqueId(), storage);

            var now = System.currentTimeMillis();
            var pardonAt = data.getPardonAt();

            if (now < pardonAt) {
                return TagResult.FAILURE;
            }

            var value = switch (data.configuration().reviveHeartsMode()) {
                case USE_REVIVE_HEARTS -> data.configuration().reviveHearts() * 2;
                case USE_DEFAULT_HEARTS -> generalSection.<Double>getValue("defaultHearts") * 2D;
            };

            player.setFallDistance(0);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(value);
            player.setHealth(value);

            final var reviver = storage.getString("reviver");

            for (final var command : data.configuration().reviveCommands()) {
                if (command.isBlank()) continue;

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), data.formatMessage(command).replace("{{reviver}}", reviver));
            }

            instance.remove();

            return TagResult.SUCCESS;
        });

        tag.on(TagBehaviors.ADD, UniqueIdentifier.combine(init.getName().toLowerCase(Locale.ROOT), "initialize"), (context, storage, instance) -> {
            var id = context.target();

            var player = Helix.players().getOnline(id);

            if (player == null) return TagResult.FAILURE;

            var info = EliminationHelper.getInfoFor(player, this.eliminationConfiguration.getCompoundArray("eliminations"), storage);
            var banTime = info.configuration().banTime() < 0 ? Long.MAX_VALUE : info.configuration().banTime();

            storage.setLong("eliminatedAt", info.eliminatedAt());
            storage.setLong("banTime", banTime);
            storage.setString("playerMessage", info.configuration().playerMessage());
            storage.setByte("notifMode", (byte) info.configuration().notificationMode().ordinal());
            storage.setString("notifMsg", info.configuration().notificationMessage());
            storage.setByte("reviveMode", (byte) info.configuration().reviveHeartsMode().ordinal());
            storage.setDouble("reviveHearts", info.configuration().reviveHearts());
            storage.setBoolean("shouldBanPlayer", info.configuration().shouldBanPlayer());
            storage.setByteArray("eliminationCommands", info.configuration().eliminationCommandsAsBytes());
            storage.setByteArray("reviveCommands", info.configuration().reviveCommandsAsBytes());

            this.eliminatedList.removeIf(eliminationInfo -> eliminationInfo.player().getUniqueId().equals(context.target()));
            this.eliminatedList.add(info);

            return TagResult.SUCCESS;
        });

        tag.on(TagBehaviors.REMOVE, UniqueIdentifier.combine(init.getName().toLowerCase(Locale.ROOT), "teardown"), (context, storage, instance) -> {
            this.eliminatedList.removeIf(eliminationInfo -> eliminationInfo.player().getUniqueId().equals(context.target()));

            return TagResult.SUCCESS;
        });
    }

    public void reloadConfig(LifestealInit init) {
        try {
            this.mainConfiguration.loadOrCreate();
            this.heartConfiguration.loadOrCreate();
            this.beaconConfiguration.loadOrCreate();
            this.eliminationConfiguration.loadOrCreate();
            this.commandConfiguration.loadOrCreate();

            this.loadItems(init);
        } catch (Throwable throwable) {
            HelixLogger.reportError(throwable);
        }
    }

    public void onDisable(LifestealInit init) {

    }

    private void convertOldConfigs() {
        var oldData = new File(((BukkitHelixProvider) Helix.provider()).getDataFolder().getParentFile(), "LifeSteal-Smp-Plugin");
        var converted = new File(oldData, "converted.mark");

        if (!oldData.isDirectory() || converted.isFile()) {
            return;
        }

        try {
            if(!converted.createNewFile()) {
                HelixLogger.error("Unable to mark old data as converted! Will not convert old configs.");
                return;
            }
        } catch (IOException exception) {
            HelixLogger.reportError(exception);
            HelixLogger.error("Error while creating converted mark file! Aborting conversion.");
            return;
        }

        HelixLogger.info("Lifesteal is attempting to convert over some options...");
        var main = new File(oldData, "config.yml");

        if (main.isFile()) {
            var conf = new YamlConfiguration();
            try {
                conf.load(main);
            } catch (IOException | InvalidConfigurationException e) {
                HelixLogger.error("Unable to load lifesteal old main configuration!");
                HelixLogger.reportError(e);
            }
            ConfigurationConverter.convertMain(conf, this.mainConfiguration);
        }

        var bans = new File(oldData, "bans.yml");
        if (bans.isFile()) {
            var conf = new YamlConfiguration();
            try {
                conf.load(bans);
            } catch (IOException | InvalidConfigurationException e) {
                HelixLogger.error("Unable to load lifesteal old ban configuration!");
                HelixLogger.reportError(e);
            }
            ConfigurationConverter.convertBans(conf, this.eliminationConfiguration);
        }

        HelixLogger.info("Lifesteal has converted it's old config to the latest format!");
    }

    private void loadItems(LifestealInit init) {
         var recipes = new LinkedHashMap<String, RecipeConfiguration>();
         var itemRegistry = Helix.items();

        var general = this.mainConfiguration.child("general");

        var easterEggs = this.mainConfiguration.child("general").<Boolean>getValue("easterEggs");

        var baconId =  UniqueIdentifier.combine(init.getName().toLowerCase(Locale.ROOT), "bacon");
        var baconItem = new BaconItem();
        itemRegistry.register(baconId, baconItem);

        var baconStack = itemRegistry.createItem(baconId, baconItem.defaultDisplayData());

        for (var config : this.heartConfiguration.<HeartItemConfiguration>getCompoundArray("heartItems")) {
            if (!config.isEnabled()) continue;

            var key = UniqueIdentifier.combine(init.getName().toLowerCase(Locale.ROOT), config.getId());
            itemRegistry.register(key, new HeartItem(general, config));

            if (!config.isCraftable()) continue;

            addCustomItemRecipe(recipes, key, config.getDisplayData(), config.getRecipeConfiguration(), config.getId());
        }

        for (var config : this.beaconConfiguration.<ReviveBeaconConfiguration>getCompoundArray("beaconItems")) {
            if(!config.isEnabled()) continue;

            var key = UniqueIdentifier.combine(init.getName().toLowerCase(Locale.ROOT), config.getId());
            itemRegistry.register(key, new ReviveBeaconItem(config));

            if (!config.isCraftable()) continue;

            addCustomItemRecipe(recipes, key, config.getDisplayData(), config.getRecipeConfiguration(), config.getId());
        }

        var screens = Helix.screens();
        if(!screens.register(RECIPE_SCREEN_ID, new RecipeScreen(recipes))) {
            HelixLogger.error("Unable to register recipe_screen as it already exists!");
        }

        if (!easterEggs) return;

        // Create bacon recipe
        var furnace = new FurnaceRecipe(
                Objects.requireNonNull(NamespacedKey.fromString("revive_bacon")),
                baconStack,
                new RecipeChoice.MaterialChoice(Material.BEACON),
                50,
                20 * 10
        );

        Bukkit.addRecipe(furnace);
    }

    private void addCustomItemRecipe(LinkedHashMap<String, RecipeConfiguration> recipes, UniqueIdentifier key, ItemDisplayData displayData, RecipeConfiguration recipeConfiguration, String id) {
        var defaultStack = Helix.items().createItem(key, displayData);

        var recipe = recipeConfiguration.createRecipe(NamespacedKey.fromString(key.toString()), defaultStack);

        Bukkit.addRecipe(recipe);

        recipes.put(id, recipeConfiguration);
    }


}
