package me.ikevoodoo.lssmp.elimination.context;

public record SwitchMode(Type type, String id) {

    public enum Type {
        NO_SWITCH,
        SWITCH_TO
    }

}
