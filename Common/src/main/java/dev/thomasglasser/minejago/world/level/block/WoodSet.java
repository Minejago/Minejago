package dev.thomasglasser.minejago.world.level.block;

import dev.thomasglasser.minejago.registration.BlockRegistryObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;

import java.util.function.Supplier;

public record WoodSet(ResourceLocation id,
                      BlockRegistryObject<Block> planks,
                      BlockRegistryObject<Block> sapling,
                      BlockRegistryObject<Block> log,
                      BlockRegistryObject<Block> strippedLog,
                      BlockRegistryObject<Block> wood,
                      BlockRegistryObject<Block> strippedWood,
                      BlockRegistryObject<Block> leaves,
                      BlockRegistryObject<Block> pottedSapling,
                      AbstractTreeGrower treeGrower,
                      Supplier<TagKey<Block>> logsBlockTag,
                      Supplier<TagKey<Item>> logsItemTag)
{}
