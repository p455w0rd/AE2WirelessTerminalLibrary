package p455w0rd.ae2wtlib.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.init.LibItems;
import p455w0rd.ae2wtlib.init.LibRecipes;
import p455w0rd.ae2wtlib.items.ItemWUT;

/**
 * @author p455w0rd
 *
 */
public class RecipeAddTerminal extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe, IWUTRecipe {

	private static List<RecipeAddTerminal> REGISTRY = new ArrayList<>();
	private static int c = 0;

	ItemStack wut = ItemStack.EMPTY;
	ItemStack terminalB = ItemStack.EMPTY;
	ItemStack output = ItemStack.EMPTY;

	public RecipeAddTerminal(final ItemStack wut, final ItemStack terminalB) {
		this.wut = wut;
		this.terminalB = terminalB;
	}

	private static List<RecipeAddTerminal> getRegistry() {
		return REGISTRY;
	}

	public static List<RecipeAddTerminal> getRecipes() {
		if (getRegistry().size() > 0) {
			getRegistry().clear();
		}

		// we only want recipes with WUTs that have all terminals added
		// already except for the one being added in the recipe
		for (final ICustomWirelessTerminalItem wt : ItemWUT.getIntegratableTypes()) {
			ItemStack preAddWut = new ItemStack(LibItems.ULTIMATE_TERMINAL);
			final ItemStack currentTermA = new ItemStack((Item) wt);
			for (final ICustomWirelessTerminalItem wt2 : ItemWUT.getIntegratableTypes()) {
				final ItemStack currentTermB = new ItemStack((Item) wt2);
				if (wt != wt2 && !LibRecipes.isEitherCreative(currentTermA, currentTermB) && !LibRecipes.isEitherWut(currentTermA, currentTermB) && currentTermA.getItem() != currentTermB.getItem()) {
					if (!preAddWut.hasTagCompound()) {
						preAddWut = ItemWUT.createNewWUT(currentTermA, currentTermB);
					}
					else {
						if (!ItemWUT.isTypeInstalled(preAddWut, currentTermB.getItem())) {
							final int numStored = ItemWUT.getStoredTerminalStacks(preAddWut).size();
							final int numTypes = ItemWUT.getIntegratableTypes().size();
							if (numStored == numTypes - 1) {
								addRecipe(preAddWut, currentTermB);
								break;
							}
							else {
								preAddWut = ItemWUT.addTerminal(preAddWut, currentTermB);
								addRecipe(preAddWut, currentTermB);
							}
						}
					}
				}
			}
		}
		return getRegistry();
	}

	public static RecipeAddTerminal[] getRecipesArray() {
		return getRecipes().toArray(new RecipeAddTerminal[getRecipes().size()]);
	}

	public static RecipeAddTerminal addRecipe(final ItemStack wut, final ItemStack terminalB) {
		if (WTApi.instance().getWUTUtility().isWUT(wut) && !WTApi.instance().getWUTUtility().isWUT(terminalB) && WTApi.instance().isAnyWT(terminalB) && !LibRecipes.isEitherCreative(wut, terminalB)) {
			final RecipeAddTerminal newRecipe = (RecipeAddTerminal) new RecipeAddTerminal(wut, terminalB).setRegistryName(WTApi.MODID, "wut_add_" + c++);
			final List<IWUTRecipe> rl = new ArrayList<>();
			for (final RecipeAddTerminal r : getRegistry()) {
				rl.add(r);
			}
			if (LibRecipes.isRecipeRegistered(rl, newRecipe)) {
				return null;
			}
			final List<IWUTRecipe> registry = new ArrayList<>();
			for (final RecipeAddTerminal r : getRegistry()) {
				registry.add(r);
			}
			if (!LibRecipes.isRecipeRegistered(registry, newRecipe) && !LibRecipes.isEitherCreative(wut, terminalB)) {
				getRegistry().add(newRecipe);
				return newRecipe;
			}
		}
		return null;
	}

	@Override
	public boolean isSame(final IWUTRecipe recipe) {
		return LibRecipes.areStacksEqual(getTerminalA(), recipe.getTerminalA()) && LibRecipes.areStacksEqual(getTerminalB(), recipe.getTerminalB());
	}

	@Override
	public ItemStack getTerminalA() {
		return wut;
	}

	@Override
	public ItemStack getTerminalB() {
		return terminalB;
	}

	@Override
	public String getGroup() {
		return WTApi.MODID + ":wut_add";
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemWUT.addTerminal(wut, terminalB);
	}

	@Override
	public boolean matches(final InventoryCrafting inv, final World world) {
		final List<ItemStack> inputs = Lists.newArrayList();
		for (int i = 0; i < inv.getHeight(); ++i) {
			for (int j = 0; j < inv.getWidth(); ++j) {
				final ItemStack itemstack = inv.getStackInRowAndColumn(j, i);
				if (!itemstack.isEmpty()) {
					inputs.add(itemstack);
				}
			}
		}
		if (inputs.size() == 2) {
			if (WTApi.instance().getWUTUtility().isWUT(inputs.get(0))) {
				final boolean isInstalled = ItemWUT.isTypeInstalled(inputs.get(0), inputs.get(1).getItem());
				if (WTApi.instance().isAnyWT(inputs.get(1)) && !isInstalled) {
					//wut = inputs.get(0);
					//terminalB = inputs.get(1);
					output = ItemWUT.addTerminal(inputs.get(0), inputs.get(1));
					return true;
				}
			}
			if (WTApi.instance().getWUTUtility().isWUT(inputs.get(1))) {
				if (WTApi.instance().isAnyWT(inputs.get(0)) && !ItemWUT.isTypeInstalled(inputs.get(1), inputs.get(0).getItem())) {
					//wut = inputs.get(1);
					//terminalB = inputs.get(0);
					output = ItemWUT.addTerminal(inputs.get(0), inputs.get(1));
					return true;
				}
			}
		}
		//wut = ItemStack.EMPTY;
		//terminalB = ItemStack.EMPTY;
		output = ItemStack.EMPTY;
		return false;
	}

	@Override
	public ItemStack getCraftingResult(final InventoryCrafting inv) {
		matches(inv, null);
		return output;
	}

	@Override
	public boolean canFit(final int width, final int height) {
		return width * height >= 2;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		final NonNullList<Ingredient> list = NonNullList.create();
		list.add(Ingredient.fromStacks(wut));
		list.add(Ingredient.fromStacks(terminalB));
		return list;
	}

}
