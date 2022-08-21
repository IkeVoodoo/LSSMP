package me.ikevoodoo.lssmp.language;

import dev.refinedtech.configlang.ConfigSection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class YamlConfigSection extends ConfigSection {

    private final ConfigurationSection section;

    private YamlConfigSection(ConfigurationSection section) {
        this.section = section;
    }

    public static YamlConfigSection of(ConfigurationSection section) {
        return new YamlConfigSection(section);
    }

    @Override
    public String getName() {
        return this.section.getName();
    }

    @Override
    public String getPath() {
        return this.section.getCurrentPath();
    }

    @Override
    public boolean isConfigSection(String s) {
        return this.section.isConfigurationSection(s);
    }

    @Override
    public Optional<ConfigSection> getConfigSection(String s) {
        return Optional.of(YamlConfigSection.of(this.section.getConfigurationSection(s)));
    }

    @Override
    public Set<String> getKeys(boolean b) {
        if (this.section == null) {
            return Set.of();
        }
        return this.section.getKeys(b);
    }

    @Override
    public boolean contains(String s) {
        return this.section.contains(s);
    }

    @Override
    public <T> Optional<T> get(String s) {
        try {
            Object obj = this.section.get(s);
            if (obj == null) return Optional.empty();
            return Optional.of((T) obj);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public <T> T get(String s, T t) {
        return this.<T>get(s).orElse(t);
    }

    @Override
    public Optional<Object> getObject(String s) {
        return Optional.ofNullable(this.section.get(s));
    }

    @Override
    public Object getObject(String s, Object o) {
        return this.getObject(s).orElse(o);
    }

    @Override
    public void set(String s, Object o) {
        this.section.set(s, o);
    }

    @Override
    public void save(File file) throws IOException {
        if (this.section instanceof YamlConfiguration) {
            ((YamlConfiguration) this.section).save(file);
        }
    }

    @Override
    public void load(File file) throws IOException {
        try {
            if (this.section instanceof YamlConfiguration) {
                ((YamlConfiguration) this.section).load(file);
            }
        } catch (InvalidConfigurationException e) {
            throw new IOException(e);
        }
    }
}
