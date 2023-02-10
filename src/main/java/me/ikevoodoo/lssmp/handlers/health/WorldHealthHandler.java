package me.ikevoodoo.lssmp.handlers.health;

import me.ikevoodoo.smpcore.utils.PDCUtils;
import me.ikevoodoo.smpcore.utils.Pair;
import me.ikevoodoo.smpcore.utils.health.HealthHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;
import java.util.function.Function;

public class WorldHealthHandler implements HealthHandler {

    private final Function<World, NamespacedKey> worldKeyCreator;

    public WorldHealthHandler(Function<World, NamespacedKey> worldKeyCreator) {
        this.worldKeyCreator = worldKeyCreator;
    }

    @Override
    public void setMaxHealth(LivingEntity livingEntity, double amount) {
        this.setMaxHealth(livingEntity, amount, livingEntity.getWorld());
    }

    @Override
    public void setMaxHealth(LivingEntity livingEntity, double amount, World world) {
        var key = this.worldKeyCreator.apply(world);
        var pdc = livingEntity.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.DOUBLE, amount);

        var attrib = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attrib == null)
            return;

        attrib.setBaseValue(amount);
    }

    @Override
    public double getMaxHealth(LivingEntity livingEntity) {
        var world = livingEntity.getWorld();
        var container = livingEntity.getPersistentDataContainer();
        Optional<Pair<String, Double>> amount = PDCUtils.getPartial(container, world.getUID().toString(), PersistentDataType.DOUBLE);
        if (amount.isPresent())
            return amount.get().getSecond();

        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        return instance == null ? 20 : instance.getBaseValue();
    }
}
