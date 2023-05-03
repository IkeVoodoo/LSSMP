package me.ikevoodoo.lssmp.config.bans;

import me.ikevoodoo.lssmp.config.errors.ConfigError;
import me.ikevoodoo.smpcore.handlers.EliminationData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BanConfig {

    public static final BanConfig INSTANCE = new BanConfig();

    // Name -> BanTimeData
    private final Map<String, BanTimeData> banTimeDatas = new HashMap<>();

    private BanConfig() {

    }

    public List<ConfigError> load(ConfigurationSection section) {
        if (section == null)
            return List.of(new ConfigError(null, "Section is null"));

        this.banTimeDatas.clear();

        var errors = new ArrayList<ConfigError>();

        for (var key : section.getKeys(false)) {
            var data = BanTimeData.fromConfig(section.getConfigurationSection(key));
            if (data.isError()) {
                errors.add(data.error().addKey(key));
                continue;
            }

            this.banTimeDatas.put(key, data.key());
        }

        return errors;
    }

    public BanTimeData findByPermission(String permission) {
        for (var entry : this.banTimeDatas.entrySet()) {
            if (permission.equalsIgnoreCase(entry.getValue().permission()))
                return entry.getValue();
        }

        return null;
    }

    public BanTimeData findByPermission(Permission permission) {
        return findByPermission(permission.getName());
    }

    public BanTimeData findHighest(Permissible permissible) {
        BanTimeData highest = null;
        for (var entry : this.banTimeDatas.entrySet()) {
            if (!permissible.hasPermission(entry.getValue().permission()))
                continue;

            if(highest == null || highest.time() < entry.getValue().time())
                highest = entry.getValue();
        }

        return highest;
    }

    public EliminationData findHighest(Permissible permissible, String fallbackMessage, long fallbackTime) {
        var data = BanConfig.INSTANCE.findHighest(permissible);

        var banMessage = data == null ? fallbackMessage : data.banMessage();
        var time = data == null ? fallbackTime : data.time();

        return new EliminationData(banMessage, time);
    }

    public BanTimeData getByName(String name) {
        return this.banTimeDatas.get(name);
    }

    public Map<String, BanTimeData> getBanTimeDatas() {
        return new HashMap<>(this.banTimeDatas);
    }
}
