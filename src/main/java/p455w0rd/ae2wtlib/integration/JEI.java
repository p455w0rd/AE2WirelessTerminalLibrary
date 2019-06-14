package p455w0rd.ae2wtlib.integration;

import javax.annotation.Nonnull;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
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
	public void register(@Nonnull IModRegistry registry) {
		//CraftingRecipeChecker.getValidRecipes(registry.getJeiHelpers());
		//registry.addRecipes(Lists.newArrayList(LibRecipes.NEW_UNIVERSAL_TERMINAL), VanillaRecipeCategoryUid.CRAFTING);
		//registry.addRecipes(Lists.newArrayList(LibRecipes.ADD_UNIVERSAL_TERMINAL), VanillaRecipeCategoryUid.CRAFTING);
		registry.addIngredientInfo(new ItemStack(LibItems.ULTIMATE_TERMINAL), VanillaTypes.ITEM, "jei.wut.desc");
		registry.handleRecipes(RecipeNewTerminal.class, recipe -> new UltimateTerminalWrapper<RecipeNewTerminal>(registry.getJeiHelpers(), recipe), VanillaRecipeCategoryUid.CRAFTING);
		registry.handleRecipes(RecipeAddTerminal.class, recipe -> new UltimateTerminalWrapper<RecipeAddTerminal>(registry.getJeiHelpers(), recipe), VanillaRecipeCategoryUid.CRAFTING);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry registry) {
	}

	//private List<IRecipe> getValidRecipes(IRecipe recipe) {
	//CraftingRecipeValidator<RecipeNewTerminal> shapedOreRecipeValidator = new CraftingRecipeValidator<>(recipe -> new ShapedOreRecipeWrapper(jeiHelpers, recipe));
	//}

	public static boolean isIngrediantOverlayActive() {
		return mezz.jei.config.Config.isOverlayEnabled();
	}

	public static class UltimateTerminalWrapper<T extends IRecipe> extends ShapelessRecipeWrapper<T> implements IShapedCraftingRecipeWrapper {

		public UltimateTerminalWrapper(IJeiHelpers jeiHelpers, T recipe) {
			super(jeiHelpers, recipe);
		}

		@Override
		public int getWidth() {
			return 3;
		}

		@Override
		public int getHeight() {
			return 3;
		}

	}

}
