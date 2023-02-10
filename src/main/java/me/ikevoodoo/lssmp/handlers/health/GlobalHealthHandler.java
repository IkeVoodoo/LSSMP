package me.ikevoodoo.lssmp.handlers.health;

import me.ikevoodoo.smpcore.utils.health.HealthHandler;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

public class GlobalHealthHandler implements HealthHandler {

    @Override
    public void setMaxHealth(LivingEntity livingEntity, double amount) {
        var attrib = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attrib == null)
            return;

        attrib.setBaseValue(amount);
    }

    @Override
    public void setMaxHealth(LivingEntity livingEntity, double amount, World world) {
        setMaxHealth(livingEntity, amount);
    }

    @Override
    public double getMaxHealth(LivingEntity livingEntity) {
        var attrib = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attrib == null)
            return 20.0;

        return attrib.getBaseValue();
    }
}
