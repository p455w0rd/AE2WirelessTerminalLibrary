package p455w0rd.ae2wtlib.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.init.LibRecipes;
import p455w0rd.ae2wtlib.items.ItemWUT;

/**
 * @author p455w0rd
 *
 * This recipe is used when combining two {@link ICustomWirelessTerminalItem}s
 * which are not instances of {@link ItemWUT}
 */
public class RecipeNewTerminal extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe, IWUTRecipe {

	private static List<RecipeNewTerminal> REGISTRY = new ArrayList<>();
	private static int c = 0;

	ItemStack terminalA = ItemStack.EMPTY;
	ItemStack terminalB = ItemStack.EMPTY;
	ItemStack output = ItemStack.EMPTY;

	private RecipeNewTerminal(final ItemStack terminalA, final ItemStack terminalB) {
		this.terminalA = terminalA;
		this.terminalB = terminalB;
	}

	private static List<RecipeNewTerminal> getRegistry() {
		return REGISTRY;
	}

	public static List<RecipeNewTerminal> getRecipes() {
		return getRegistry();
	}

	public static RecipeNewTerminal[] getRecipesArray() {
		return getRecipes().toArray(new RecipeNewTerminal[getRecipes().size()]);
	}

	public static void addRecipe(final ItemStack terminalA, final ItemStack terminalB) {
		final RecipeNewTerminal newRecipe = (RecipeNewTerminal) new RecipeNewTerminal(terminalA, terminalB).setRegistryName(WTApi.MODID, "wut_new_" + c++);
		final List<IWUTRecipe> registry = new ArrayList<>();
		for (final RecipeNewTerminal r : getRegistry()) {
			registry.add(r);
		}
		if (!LibRecipes.isRecipeRegistered(registry, newRecipe) && !LibRecipes.isEitherWut(terminalA, terminalB) && !LibRecipes.isEitherCreative(terminalA, terminalB)) {
			getRegistry().add(newRecipe);
		}
	}

	@Override
	public boolean isSame(final IWUTRecipe recipe) {
		return LibRecipes.areStacksEqual(getTerminalA(), recipe.getTerminalA()) && LibRecipes.areStacksEqual(getTerminalB(), recipe.getTerminalB());
	}

	@Override
	public ItemStack getTerminalA() {
		return terminalA;
	}

	@Override
	public ItemStack getTerminalB() {
		return terminalB;
	}

	@Override
	public String getGroup() {
		return WTApi.MODID + ":wut_new";
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemWUT.createNewWUT(terminalA, terminalB);
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
			//terminalA = inputs.get(0).copy();
			//terminalB = inputs.get(1).copy();
			final boolean notWut = !WTApi.instance().getWUTUtility().isWUT(inputs.get(0)) && !WTApi.instance().getWUTUtility().isWUT(inputs.get(1));
			if (inputs.get(0).getItem() instanceof ICustomWirelessTerminalItem && inputs.get(1).getItem() instanceof ICustomWirelessTerminalItem && inputs.get(0).getItem() != inputs.get(1).getItem() && notWut) {
				output = ItemWUT.createNewWUT(inputs.get(0), inputs.get(1));
				//inv.clear();
				return true;
			}
		}
		//terminalA = ItemStack.EMPTY;
		output = ItemStack.EMPTY;
		//terminalB = ItemStack.EMPTY;
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
		list.add(Ingredient.fromStacks(terminalA));
		list.add(Ingredient.fromStacks(terminalB));
		return list;
	}

}
