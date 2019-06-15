package p455w0rd.ae2wtlib.init;

import java.util.List;

import com.google.common.collect.Lists;

import appeng.api.AEApi;
import appeng.api.features.IWirelessTermHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.LoaderState;
import p455w0rd.ae2wtlib.AE2WTLib;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.api.item.ItemWT;
import p455w0rd.ae2wtlib.recipe.RecipeNewTerminal;

/**
 * @author p455w0rd
 *
 */
public class LibWTRegistry extends WTRegistry {

	private static final List<ICustomWirelessTerminalItem> WT_REGISTRY = Lists.newArrayList();

	@Override
	public List<ICustomWirelessTerminalItem> getRegisteredTerminals() {
		return WT_REGISTRY;
	}

	@Override
	public void registerWirelessTerminal(final ICustomWirelessTerminalItem wirelessTerminal) {
		if (AE2WTLib.PROXY.getLoaderState() != LoaderState.PREINITIALIZATION) {
			LibLogger.warn("Wireless Terminals must be registered during PreInit!");
			return;
		}
		if (wirelessTerminal != null) {
			if (!(wirelessTerminal instanceof ItemWT)) {
				LibLogger.warn("Wireless terminal items must extend ItemWT.class");
				return;
			}
			if (!(wirelessTerminal instanceof ICustomWirelessTerminalItem)) {
				LibLogger.warn("Wireless terminal items must implement ICustomWirelessTerminalItem.class");
				return;
			}
			if (WT_REGISTRY.contains(wirelessTerminal)) {
				LibLogger.warn("Terminal " + wirelessTerminal.getClass() + " has already been registered!");
				return;
			}
			addNewRecipes(wirelessTerminal);
			getRegisteredTerminals().add(wirelessTerminal);
			AE2WTLib.PROXY.registerCustomRenderer(wirelessTerminal);
		}
	}

	private void addNewRecipes(final ICustomWirelessTerminalItem wirelessTerminal) {
		if (wirelessTerminal.isCreative()) {
			return;
		}
		final Item item = (Item) wirelessTerminal;
		final ItemStack stack = new ItemStack(item);
		for (final ICustomWirelessTerminalItem wth : getRegisteredTerminals()) {
			RecipeNewTerminal.addRecipe(stack, new ItemStack((Item) wth));
		}
	}

	private static void registerTerminalWithAE2(final IWirelessTermHandler wirelessTerminal) {
		if (wirelessTerminal instanceof ItemWT) {
			AEApi.instance().registries().wireless().registerWirelessHandler(wirelessTerminal);
			AEApi.instance().registries().charger().addChargeRate((Item) wirelessTerminal, LibConfig.WT_MAX_POWER);
		}
	}

	public static void registerAllTerminalsWithAE2() {
		for (final ICustomWirelessTerminalItem wirelessTerminal : WTApi.instance().getWirelessTerminalRegistry().getRegisteredTerminals()) {
			if (wirelessTerminal instanceof ItemWT) {
				registerTerminalWithAE2(wirelessTerminal);
			}
		}
	}

}
