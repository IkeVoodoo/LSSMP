package me.ikevoodoo.lssmp.commands.setup.handlers;

public interface SetupEnumHandler<T extends Enum<T>> extends SetupCommandHandler {

    Class<T> getType();

    String description(T value);

    void onSubmit(T value);

}
