package me.ikevoodoo.lssmp.config.errors;

public record Some<T>(T key, ConfigError error) {

    public static <T> Some<T> of(T key) {
        return new Some<>(key, null);
    }

    public static <T> Some<T> error(ConfigError error) {
        return new Some<>(null, error);
    }

    public boolean isPresent() {
        return this.key != null;
    }

    public boolean isError() {
        return this.error != null;
    }

}
