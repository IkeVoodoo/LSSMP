package me.ikevoodoo.lssmp.commands.health;

import me.ikevoodoo.helix.api.commands.HelixCommand;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.config.Configuration;

public class HealthCommand extends HelixCommand {

    private final Configuration configuration;

    public HealthCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create(this.configuration.getValue("name"))
                .permission(this.configuration.getValue("permission"))
                .childCommand(new HealthAddCommand())
                .childCommand(new HealthGetCommand())
                .childCommand(new HealthSetCommand())
                .childCommand(new HealthSubCommand());
    }
}
