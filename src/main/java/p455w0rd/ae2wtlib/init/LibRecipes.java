package p455w0rd.ae2wtlib.init;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.recipe.RecipeAddTerminal;
import p455w0rd.ae2wtlib.recipe.RecipeNewTerminal;

/**
 * @author p455w0rd
 *
 */
public class LibRecipes {

	public static void register(final RegistryEvent.Register<IRecipe> event) {
		event.getRegistry().registerAll(RecipeNewTerminal.getRecipesArray());
		event.getRegistry().registerAll(RecipeAddTerminal.getRecipesArray());
	}

	public static boolean isEitherWut(final ItemStack term1, final ItemStack term2) {
		return isWut(term1) || isWut(term2);
	}

	public static boolean isWut(final ItemStack wirelessTerminal) {
		return WTApi.instance().getWUTUtility().isWUT(wirelessTerminal);
	}

	public static Boolean isEitherCreative(final ItemStack term1, final ItemStack term2) {
		return isCreative(term1) || isCreative(term2);
	}

	public static boolean isCreative(final ItemStack wirelessTerminal) {
		if (!wirelessTerminal.isEmpty()) {
			final Item item = wirelessTerminal.getItem();
			if (item instanceof ICustomWirelessTerminalItem) {
				final ICustomWirelessTerminalItem wth = (ICustomWirelessTerminalItem) item;
				return wth.isCreative();
			}
		}
		return false;
	}

	public static boolean isRecipeRegistered(final List<IWUTRecipe> registry, final IWUTRecipe recipe) {
		for (final IWUTRecipe currentRecipe : registry) {
			if (currentRecipe.isSame(recipe)) {
				return true;
			}
		}
		return false;
	}

	public static boolean areStacksEqual(final ItemStack stackA, final ItemStack stackB) {
		final ItemStack tmpStack1 = stackA.copy();
		final ItemStack tmpStack2 = stackB.copy();
		tmpStack1.setCount(1);
		tmpStack2.setCount(1);
		return ItemStack.areItemStacksEqual(stackA, stackB);
	}

}
