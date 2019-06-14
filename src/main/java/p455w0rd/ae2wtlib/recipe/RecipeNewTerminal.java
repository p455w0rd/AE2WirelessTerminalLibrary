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
import p455w0rd.ae2wtlib.init.LibGlobals;
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

	private RecipeNewTerminal(ItemStack terminalA, ItemStack terminalB) {
		this.terminalA = terminalA;
		this.terminalB = terminalB;
	}

	private static List<RecipeNewTerminal> getRegistry() {
		return REGISTRY;
	}

	public static RecipeNewTerminal[] getRecipes() {
		return getRegistry().toArray(new RecipeNewTerminal[getRegistry().size()]);
	}

	public static void addRecipe(ItemStack terminalA, ItemStack terminalB) {
		RecipeNewTerminal newRecipe = (RecipeNewTerminal) new RecipeNewTerminal(terminalA, terminalB).setRegistryName(LibGlobals.MODID, "wut_new_" + c++);
		List<IWUTRecipe> registry = new ArrayList<>();
		for (RecipeNewTerminal r : getRegistry()) {
			registry.add(r);
		}
		if (!LibRecipes.isRecipeRegistered(registry, newRecipe) && !LibRecipes.isEitherWut(terminalA, terminalB) && !LibRecipes.isEitherCreative(terminalA, terminalB)) {
			getRegistry().add(newRecipe);
		}
	}

	@Override
	public boolean isSame(IWUTRecipe recipe) {
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
		return LibGlobals.MODID + ":wut_new";
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemWUT.createNewWUT(terminalA, terminalB);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		List<ItemStack> inputs = Lists.newArrayList();
		for (int i = 0; i < inv.getHeight(); ++i) {
			for (int j = 0; j < inv.getWidth(); ++j) {
				ItemStack itemstack = inv.getStackInRowAndColumn(j, i);
				if (!itemstack.isEmpty()) {
					inputs.add(itemstack);
				}
			}
		}
		if (inputs.size() == 2) {
			//terminalA = inputs.get(0).copy();
			//terminalB = inputs.get(1).copy();
			boolean notWut = !WTApi.instance().getWUTUtility().isWUT(inputs.get(0)) && !WTApi.instance().getWUTUtility().isWUT(inputs.get(1));
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
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		matches(inv, null);
		return output;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(Ingredient.fromStacks(terminalA));
		list.add(Ingredient.fromStacks(terminalB));
		return list;
	}

}
