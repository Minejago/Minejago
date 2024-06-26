package dev.thomasglasser.minejago.plugins.jei;

import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.world.item.crafting.MinejagoRecipeTypes;
import dev.thomasglasser.minejago.world.item.crafting.TeapotBrewingRecipe;
import dev.thomasglasser.minejago.world.level.block.MinejagoBlocks;
import dev.thomasglasser.tommylib.api.client.ClientUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import javax.annotation.Nullable;
import java.util.List;

@JeiPlugin
public class MinejagoJeiPlugin implements IModPlugin
{
	@Nullable
	private TeapotBrewingRecipeCategory teapotBrewingRecipeCategory;

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		registration.addRecipeCategories(
				teapotBrewingRecipeCategory = new TeapotBrewingRecipeCategory(registration.getJeiHelpers().getGuiHelper())
		);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		if (teapotBrewingRecipeCategory != null)
			registration.addRecipes(teapotBrewingRecipeCategory.getRecipeType(), getTeapotBrewingRecipes());
	}

	public List<RecipeHolder<TeapotBrewingRecipe>> getTeapotBrewingRecipes() {
		return ClientUtils.getMinecraft().level.getRecipeManager().getAllRecipesFor(MinejagoRecipeTypes.TEAPOT_BREWING.get());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
	{
		if (teapotBrewingRecipeCategory != null)
			registration.addRecipeCatalyst(MinejagoBlocks.TEAPOT.get().asItem().getDefaultInstance(), teapotBrewingRecipeCategory.getRecipeType());
	}

	@Override
	public ResourceLocation getPluginUid()
	{
		return Minejago.modLoc("jei");
	}
}
