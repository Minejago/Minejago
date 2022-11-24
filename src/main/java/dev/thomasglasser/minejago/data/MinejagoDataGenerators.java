package dev.thomasglasser.minejago.data;

import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.data.blockstates.MinejagoBlockStates;
import dev.thomasglasser.minejago.data.lang.MinejagoEnUsLanguage;
import dev.thomasglasser.minejago.data.loot.MinejagoLootTables;
import dev.thomasglasser.minejago.data.models.MinejagoItemModels;
import dev.thomasglasser.minejago.data.recipes.MinejagoRecipes;
import dev.thomasglasser.minejago.data.sounds.MinejagoSoundDefinitions;
import dev.thomasglasser.minejago.data.tags.MinejagoBannerPatternTags;
import dev.thomasglasser.minejago.data.tags.MinejagoBlockTags;
import dev.thomasglasser.minejago.data.tags.MinejagoItemTags;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

public class MinejagoDataGenerators
{
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        boolean onServer = event.includeServer();
        boolean onClient = event.includeClient();

        MinejagoBlockTags blockTags = new MinejagoBlockTags(generator, existingFileHelper);

        //Server
        generator.addProvider(onServer, new MinejagoItemTags(generator, blockTags, existingFileHelper));
        generator.addProvider(onServer, new MinejagoRecipes(generator));
        generator.addProvider(onServer, blockTags);
        generator.addProvider(onServer, new MinejagoBannerPatternTags(generator, existingFileHelper));
        generator.addProvider(onServer, new MinejagoLootTables(generator));

        //Client
        generator.addProvider(onClient, new MinejagoItemModels(generator, existingFileHelper));
        generator.addProvider(onClient, new MinejagoEnUsLanguage(generator));
        generator.addProvider(onClient, new MinejagoBlockStates(generator, existingFileHelper));
        generator.addProvider(onClient, new MinejagoSoundDefinitions(generator, existingFileHelper));
    }
}
