package dev.thomasglasser.minejago.data.powers;

import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.core.particles.MinejagoParticleTypes;
import dev.thomasglasser.minejago.core.particles.SpinjitzuParticleOptions;
import dev.thomasglasser.minejago.world.entity.power.MinejagoPowers;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;

public class MinejagoPowerDatagenSuite extends PowerDatagenSuite {
    public MinejagoPowerDatagenSuite(GatherDataEvent event, LanguageProvider languageProvider) {
        super(event, Minejago.MOD_ID, languageProvider::add);
    }

    @Override
    public void generate() {
        makePowerSuite(MinejagoPowers.NONE);
        makePowerSuite(MinejagoPowers.ICE, builder -> builder
                .color(ChatFormatting.WHITE)
                .defaultTagline()
                .mainSpinjitzuColor(SpinjitzuParticleOptions.ELEMENT_LIGHT_BLUE)
                .altSpinjitzuColor(SpinjitzuParticleOptions.ELEMENT_WHITE)
                .hasSets()
                .defaultDisplay(),
            MinejagoParticleTypes.SNOWS, "snow", 4, true, config -> {});
        makePowerSuite(MinejagoPowers.EARTH, builder -> builder
                .color(ChatFormatting.DARK_GRAY)
                .defaultTagline()
                .mainSpinjitzuColor(SpinjitzuParticleOptions.ELEMENT_BROWN)
                .altSpinjitzuColor(SpinjitzuParticleOptions.ELEMENT_TAN)
                .hasSets()
                .defaultDisplay(),
            MinejagoParticleTypes.ROCKS, "rock", 4, true, config ->
                config.tagline("Solid as rock."));
        makePowerSuite(MinejagoPowers.FIRE, builder -> builder
                .color(ChatFormatting.RED)
                .defaultTagline()
                .mainSpinjitzuColor(SpinjitzuParticleOptions.ELEMENT_ORANGE)
                .altSpinjitzuColor(SpinjitzuParticleOptions.ELEMENT_YELLOW)
                .hasSets()
                .defaultDisplay(),
            MinejagoParticleTypes.SPARKS, "spark", 4, true, config ->
                config.tagline("It burns bright in you."));
        makePowerSuite(MinejagoPowers.LIGHTNING, builder -> builder
                .color(ChatFormatting.BLUE)
                .defaultTagline()
                .mainSpinjitzuColor(SpinjitzuParticleOptions.ELEMENT_BLUE)
                .altSpinjitzuColor(SpinjitzuParticleOptions.ELEMENT_LIGHT_BLUE)
                .hasSets()
                .defaultDisplay(),
            MinejagoParticleTypes.BOLTS, "bolt", 4, true, config -> {});
        makePowerSuite(MinejagoPowers.CREATION, builder -> builder
                .color(ChatFormatting.GOLD)
                .mainSpinjitzuColor(SpinjitzuParticleOptions.ELEMENT_DARK_GOLD)
                .altSpinjitzuColor(SpinjitzuParticleOptions.ELEMENT_GOLD)
                .defaultDisplay()
                .isSpecial(),
            MinejagoParticleTypes.SPARKLES, "sparkle", 4, false, config -> {});
    }
}
