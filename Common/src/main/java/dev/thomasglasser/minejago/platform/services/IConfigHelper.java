package dev.thomasglasser.minejago.platform.services;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public interface IConfigHelper {
    void registerConfig(ModConfig.Type type, ForgeConfigSpec spec);
}
