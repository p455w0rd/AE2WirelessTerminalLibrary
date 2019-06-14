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

	/*
	public static final IRecipe NEW_UNIVERSAL_TERMINAL = new RecipeNewTerminal().setRegistryName(new ResourceLocation(LibGlobals.MODID, "wut_new"));
	public static final IRecipe ADD_UNIVERSAL_TERMINAL = new RecipeAddTerminal().setRegistryName(new ResourceLocation(LibGlobals.MODID, "wut_add"));
	private static final IRecipe[] ARRAY = new IRecipe[] {
			NEW_UNIVERSAL_TERMINAL, ADD_UNIVERSAL_TERMINAL
	};
	*/

	public static void register(RegistryEvent.Register<IRecipe> event) {
		/*
		List<ICustomWirelessTerminalItem> terminals = (List<ICustomWirelessTerminalItem>) WTApi.instance().getRegistry().getRegisteredTerminals();
		Iterator<ICustomWirelessTerminalItem> itr = terminals.iterator();
		while (itr.hasNext()) {
			ICustomWirelessTerminalItem term = itr.next();
			if (term instanceof ItemWUT || term.isCreative()) {
				itr.remove();
			}
		}
		for (int i = 0; i < terminals.size(); i++) {
			ItemStack inputA = new ItemStack((Item) terminals.get(i), 1, OreDictionary.WILDCARD_VALUE);
			for (int j = 0; j < terminals.size(); j++) {
				if (i == j) {
					continue;
				}
				ItemStack inputB = new ItemStack((Item) terminals.get(j), 1, OreDictionary.WILDCARD_VALUE);
				ItemStack wut = ItemWUT.createNewWUT(inputA, inputB);
				NonNullList<Ingredient> il = NonNullList.create();
				il.add(Ingredient.fromStacks(inputA));
				il.add(Ingredient.fromStacks(inputB));
				event.getRegistry().register(new ShapelessOreRecipe(new ResourceLocation(LibGlobals.MODID + ":wut_new"), wut, il).setRegistryName(new ResourceLocation(LibGlobals.MODID, "wut_new_" + (i + j * terminals.size()))));
			}
		}
		*/
		//event.getRegistry().register(ADD_UNIVERSAL_TERMINAL);
		event.getRegistry().registerAll(RecipeNewTerminal.getRecipes());
		event.getRegistry().registerAll(RecipeAddTerminal.getRecipes());
	}

	public static boolean isEitherWut(ItemStack term1, ItemStack term2) {
		return isWut(term1) || isWut(term2);
	}

	public static boolean isWut(ItemStack wirelessTerminal) {
		return WTApi.instance().getWUTUtility().isWUT(wirelessTerminal);
	}

	public static Boolean isEitherCreative(ItemStack term1, ItemStack term2) {
		return isCreative(term1) || isCreative(term2);
	}

	public static boolean isCreative(ItemStack wirelessTerminal) {
		if (!wirelessTerminal.isEmpty()) {
			Item item = wirelessTerminal.getItem();
			if (item instanceof ICustomWirelessTerminalItem) {
				ICustomWirelessTerminalItem wth = (ICustomWirelessTerminalItem) item;
				return wth.isCreative();
			}
		}
		return false;
	}

	public static boolean isRecipeRegistered(List<IWUTRecipe> registry, IWUTRecipe recipe) {
		for (IWUTRecipe currentRecipe : registry) {
			if (currentRecipe.isSame(recipe)) {
				return true;
			}
		}
		return false;
	}

	public static boolean areStacksEqual(ItemStack stackA, ItemStack stackB) {
		ItemStack tmpStack1 = stackA.copy();
		ItemStack tmpStack2 = stackB.copy();
		tmpStack1.setCount(1);
		tmpStack2.setCount(1);
		return ItemStack.areItemStacksEqual(stackA, stackB);
	}

}
