package me.ikevoodoo.lssmp.commands.setup.handlers;

import org.bukkit.entity.Player;

public interface SetupCommandHandler {

    String name();

    String description();

    String summary();

    default void onSubmit(Player player) {

    }

    default boolean isCommand() {
        return true;
    }

}
