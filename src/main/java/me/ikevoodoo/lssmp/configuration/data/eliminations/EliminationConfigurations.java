package me.ikevoodoo.lssmp.configuration.data.eliminations;

import org.bukkit.permissions.Permissible;

public record EliminationConfigurations(EliminationConfiguration[] eliminations) {

    public int findHighestConfigurationIndex(Permissible permissible) {
        for (int i = this.eliminations.length - 1; i > 0; i--) {
            final var conf = this.eliminations[i];
            if (conf.permission() == null) {
                return i;
            }

            if (permissible.hasPermission(conf.permission())) {
                return i;
            }
        }

        return -1;
    }

    public EliminationConfiguration findHighestConfiguration(Permissible permissible) {
        final var highest = this.findHighestConfigurationIndex(permissible);

        if (highest == -1) {
            return EliminationConfiguration.empty();
        }

        return this.eliminations[highest];
    }

}
