package dev.thomasglasser.minejago.platform;

import dev.thomasglasser.minejago.platform.services.IDataHelper;
import dev.thomasglasser.minejago.world.level.storage.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ForgeDataHelper implements IDataHelper
{

    @Override
    public PowerData getPowerData(LivingEntity entity) {
        PowerCapability capability = entity.getCapability(PowerCapabilityAttacher.POWER_CAPABILITY).orElse(new PowerCapability(entity));
        return new PowerData(capability.getPower());
    }

    @Override
    public void setPowerData(PowerData data, LivingEntity entity) {
        entity.getCapability(PowerCapabilityAttacher.POWER_CAPABILITY).ifPresent(cap -> cap.setPower(data.power()));
    }

    @Override
    public SpinjitzuData getSpinjitzuData(LivingEntity entity) {
        SpinjitzuCapability capability = entity.getCapability(SpinjitzuCapabilityAttacher.SPINJITZU_CAPABILITY).orElse(new SpinjitzuCapability(entity));
        return new SpinjitzuData(capability.isUnlocked(), capability.isActive());
    }

    @Override
    public void setSpinjitzuData(SpinjitzuData data, LivingEntity entity) {
        entity.getCapability(SpinjitzuCapabilityAttacher.SPINJITZU_CAPABILITY).ifPresent(cap ->
        {
            cap.setActive(data.active());
            cap.setUnlocked(data.unlocked());
        });
    }
}
