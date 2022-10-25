package dev.thomasglasser.minejago.world.item;

import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.data.tags.MinejagoBannerPatternTags;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MinejagoItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Minejago.MODID);

    public static final RegistryObject<Item> BAMBOO_STAFF = ITEMS.register("bamboo_staff", () -> new BambooStaffItem(Tiers.WOOD, 2, -1, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> BONE_KNIFE = ITEMS.register("bone_knife", () -> new BoneKnifeItem(MinejagoTiers.BONE_TIER, 3, -2, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> SCYTHE_OF_QUAKES = ITEMS.register("scythe_of_quakes", () -> new ScytheOfQuakesItem(new Item.Properties().defaultDurability(0).tab(CreativeModeTab.TAB_COMBAT).rarity(Rarity.EPIC).fireResistant().setNoRepair().stacksTo(1)));
    public static final RegistryObject<Item> TEACUP = ITEMS.register("teacup", () -> new TeacupItem(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
    public static final RegistryObject<Item> FILLED_TEACUP = ITEMS.register("filled_teacup", () -> new FilledTeacupItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_BREWING)));
    public static final RegistryObject<Item> FOUR_WEAPONS_BANNER_PATTERN = ITEMS.register("four_weapons_banner_pattern", () -> new BannerPatternItem(MinejagoBannerPatternTags.PATTERN_ITEM_FOUR_WEAPONS, (new Item.Properties()).stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.EPIC)));
}
