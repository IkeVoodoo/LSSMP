package me.ikevoodoo.lssmp.configuration.parsers.eliminations;

import me.ikevoodoo.helix.api.config.Configuration;
import me.ikevoodoo.helix.api.config.builder.ConfigurationBuilder;
import me.ikevoodoo.helix.api.config.parsing.CompoundTypeParser;
import me.ikevoodoo.lssmp.configuration.data.eliminations.EliminationConfiguration;
import me.ikevoodoo.lssmp.configuration.data.types.EliminationNotificationMode;
import me.ikevoodoo.lssmp.configuration.data.types.ReviveHeartsMode;
import me.ikevoodoo.lssmp.time.TimeFormatter;
import org.jetbrains.annotations.NotNull;

public class EliminationConfigurationParser implements CompoundTypeParser<EliminationConfiguration> {
    @Override
    public @NotNull Class<EliminationConfiguration> complexType() {
        return EliminationConfiguration.class;
    }

    @Override
    public @NotNull EliminationConfiguration deserialize(@NotNull Configuration configuration) {
        var perm = configuration.<String>getValue("permission");

        if ("NONE".equalsIgnoreCase(perm)) {
            perm = null;
        }

        return new EliminationConfiguration(
                configuration.getValue("kickMessage"),

                configuration.getValue("notificationMode"),
                configuration.getValue("notification"),

                perm,

                TimeFormatter.parseDuration(configuration.getValue("banTime")),

                configuration.getValue("reviveHeartsMode"),
                configuration.getValue("reviveHearts"),

                configuration.getValue("shouldBanPlayer"),
                configuration.getValueArray("eliminationCommands")
        );
    }

    @Override
    public void serialize(@NotNull Configuration configuration, @NotNull EliminationConfiguration value) {
        configuration.value("kickMessage").value(value.playerMessage());
        configuration.value("notification").value(value.notificationMessage());
        configuration.value("notificationMode").value(value.notificationMode());

        configuration.value("permission").value(value.permission());
        configuration.value("banTime").value(TimeFormatter.formatDuration(value.banTime()));

        configuration.value("reviveHeartsMode").value(value.reviveHeartsMode());
        configuration.value("reviveHearts").value(value.reviveHearts());
    }

    @Override
    public void setup(@NotNull ConfigurationBuilder template, @NotNull EliminationConfiguration value) {
        template.value("kickMessage", value.playerMessage())
                .comment("What message should players see when they get kicked?")
                .commentSpace()
                .comment("Placeholders:")
                .comment(" - {{time_remaining}}  Formats the player's remaining time until revival. Becomes \"infinite\" when the ban time is permanent.")
                .comment(" - {{revived_at}}  Formats the exact date and time when the player will be revived.")
                .comment("                   Uses  month/day/year hour:minute  for: the USA, Belize, Micronesia.")
                .comment("                   Uses  day/month/year hour:minute  for the rest of the world.")
                .comment("                   If the ban time is permanent, then it becomes \"the end of time\"")
                .comment(" - {{killer}}  The name of the player that eliminated this player.")
                .comment("               Becomes \"Environment\" when the player got killed by mobs, fall damage, etc.")
                .comment("               > Becomes \"unknown\" when the plugin is unable to get the killer's name.")
                .comment("               > This usually never happens.")
                .next();

        template.value("notification", value.notificationMessage())
                .comment("What message should be broadcast whenever a player gets eliminated?")
                .commentSpace()
                .comment("Placeholders:")
                .comment(" - {{player}}  The name of the player that got eliminated.")
                .comment(" - {{killer}}  The name of the player that eliminated this player.")
                .next();

        template.value("notificationMode", value.notificationMode())
                .comment("Who should see the ban notification?")
                .commentEnum(EliminationNotificationMode.class)
                .next();

        template.value("permission", value.permission())
                .comment("What permission is needed to access this configuration?")
                .comment("This is useful if you want to give a special ban time to certain people")
                .next();

        template.value("banTime", TimeFormatter.formatDuration(value.banTime()))
                .comment("How much time should players be banned for?")
                .comment("Use \"infinite\" to permanently ban players.")
                .comment("To ban players for a specific amount of time, use the following format:")
                .comment("0 days, 0 hours, 0 minutes, 0 seconds")
                .next();

        template.value("reviveHeartsMode", value.reviveHeartsMode())
                .comment("How many hearts should players be revived with?")
                .comment("Note: USE_DEFAULT_HEARTS will use the hearts in the general config (config.yml)")
                .commentEnum(ReviveHeartsMode.class)
                .next();

        template.value("reviveHearts", value.reviveHearts())
                .comment("How many hearts should players have when they get revived?")
                .comment("Note: Only works when reviveHeartsMode is USE_REVIVE_HEARTS")
                .next();

        template.value("shouldBanPlayer", value.shouldBanPlayer())
                .comment("Should the eliminated player be banned?")
                .next();

        template.valueArray("eliminationCommands", value.eliminationCommands())
                .comment("What commands should be ran when a player is eliminated?")
                .commentSpace()
                .comment("Placeholders:")
                .comment(" - {{player}}  The name of the player that got eliminated.")
                .comment(" - {{killer}}  The name of the player that eliminated this player.")
                .next();
    }
}
