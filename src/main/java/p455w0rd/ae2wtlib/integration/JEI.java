package p455w0rd.ae2wtlib.integration;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.item.ItemStack;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.init.LibItems;
import p455w0rd.ae2wtlib.recipe.RecipeAddTerminal;
import p455w0rd.ae2wtlib.recipe.RecipeNewTerminal;

/**
 * @author p455w0rd
 *
 */
@JEIPlugin
public class JEI implements IModPlugin {

	@Override
	public void register(final IModRegistry registry) {
		registry.addIngredientInfo(new ItemStack(LibItems.ULTIMATE_TERMINAL), VanillaTypes.ITEM, "jei.wut.desc");
		registry.addRecipes(RecipeNewTerminal.getRecipes(), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipes(RecipeAddTerminal.getRecipes(), VanillaRecipeCategoryUid.CRAFTING);
		registry.handleRecipes(RecipeNewTerminal.class, recipe -> new ShapelessRecipeWrapper<>(registry.getJeiHelpers(), recipe), VanillaRecipeCategoryUid.CRAFTING);
		registry.handleRecipes(RecipeAddTerminal.class, recipe -> new ShapelessRecipeWrapper<>(registry.getJeiHelpers(), recipe), VanillaRecipeCategoryUid.CRAFTING);
		if (!WTApi.instance().getConfig().isInfinityBoosterCardEnabled()) {
			registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(WTApi.instance().getBoosterCard()));
		}
	}

	public static boolean isIngrediantOverlayActive() {
		return mezz.jei.config.Config.isOverlayEnabled();
	}

}
