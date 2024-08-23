package me.ikevoodoo.lssmp.commands.setup.handlers;

public interface SetupBooleanHandler extends SetupCommandHandler {

    void onSubmit(boolean allow);

    default boolean isCommand() {
        return true;
    }
}
