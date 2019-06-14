package p455w0rd.ae2wtlib.api;

import net.minecraft.item.ItemStack;

/**
 * @author p455w0rd
 *
 */
public interface IWUTRecipe {

	ItemStack getTerminalA();

	ItemStack getTerminalB();

	boolean isSame(IWUTRecipe recipe);

}
