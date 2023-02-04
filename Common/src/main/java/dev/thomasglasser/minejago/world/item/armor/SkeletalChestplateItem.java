package dev.thomasglasser.minejago.world.item.armor;

import dev.thomasglasser.minejago.client.renderer.armor.SkeletalArmorRenderer;
import dev.thomasglasser.minejago.world.entity.UnderworldSkeleton;
import dev.thomasglasser.minejago.world.item.IFabricGeoItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class SkeletalChestplateItem extends ArmorItem implements IModeledArmorItem, IFabricGeoItem {
    private final UnderworldSkeleton.Variant variant;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SkeletalChestplateItem(UnderworldSkeleton.Variant variant, ArmorMaterial pMaterial, Properties pProperties) {
        super(pMaterial, EquipmentSlot.CHEST, pProperties);
        this.variant = variant;
    }

    public UnderworldSkeleton.Variant getVariant() {
        return variant;
    }

    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public GeoArmorRenderer newRenderer() {
        return new SkeletalArmorRenderer();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isSkintight() {
        return false;
    }

    public Supplier<Object> getRenderProvider() {
        return null;
    }
}
