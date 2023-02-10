package me.ikevoodoo.lssmp.config.errors;

public record ConfigError(String key, String message) {

    public ConfigError addKey(String key) {
        if (this.key == null)
            return new ConfigError(key, this.message);

        return new ConfigError(key + "." + this.key, this.message);
    }

}
