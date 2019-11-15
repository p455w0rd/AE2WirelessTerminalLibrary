package p455w0rd.ae2wtlib.init;

import java.util.*;

import com.google.common.collect.Lists;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.features.IWirelessTermHandler;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.LoaderState;
import p455w0rd.ae2wtlib.AE2WTLib;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.api.item.ItemWT;
import p455w0rd.ae2wtlib.helpers.IWirelessUniversalItem;
import p455w0rd.ae2wtlib.recipe.RecipeNewTerminal;

/**
 * @author p455w0rd
 *
 */
public class LibWTRegistry extends WTRegistry {

	private static final List<ICustomWirelessTerminalItem> WT_REGISTRY = new ArrayList<>();
	private static final Map<ICustomWirelessTerminalItem, ICustomWirelessTerminalItem> WT_TO_CREATIVE = new HashMap<>();

	@Override
	public List<ICustomWirelessTerminalItem> getRegisteredTerminals() {
		return getRegisteredTerminals(false);
	}

	@Override
	public List<ICustomWirelessTerminalItem> getRegisteredTerminals(final boolean excludeWUT) {
		if (excludeWUT) {
			final List<ICustomWirelessTerminalItem> tmpList = Lists.newArrayList(WT_REGISTRY);
			final Iterator<ICustomWirelessTerminalItem> listIterator = tmpList.iterator();
			while (listIterator.hasNext()) {
				final ICustomWirelessTerminalItem currentTerminal = listIterator.next();
				if (currentTerminal instanceof IWirelessUniversalItem) {
					listIterator.remove();
				}
			}
			return tmpList;
		}
		return WT_REGISTRY;
	}

	@Override
	public Map<ICustomWirelessTerminalItem, ICustomWirelessTerminalItem> getNonCreativeToCreativeMap() {
		return WT_TO_CREATIVE;
	}

	@Override
	public int getNumRegisteredTerminals(final boolean excludeWUT) {
		return getNonCreativeToCreativeMap().size() - (excludeWUT ? 1 : 0);
	}

	@Override
	public ItemStack getStackForHandler(final Class<? extends ICustomWirelessTerminalItem> clazz, final boolean creative, final boolean fullyPower) {
		for (final Map.Entry<ICustomWirelessTerminalItem, ICustomWirelessTerminalItem> entry : getNonCreativeToCreativeMap().entrySet()) {
			final ICustomWirelessTerminalItem currentHandler = entry.getKey();
			if (currentHandler instanceof Item) {
				if (currentHandler.getClass().equals(clazz)) {
					if (creative) {
						return new ItemStack((Item) entry.getValue());
					}
					final ItemStack terminal = new ItemStack((Item) currentHandler);
					if (fullyPower) {
						((AEBasePoweredItem) terminal.getItem()).injectAEPower(terminal, LibConfig.WT_MAX_POWER, Actionable.MODULATE);
					}
					return terminal;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public <T extends ICustomWirelessTerminalItem, C extends T> void registerWirelessTerminal(final T wirelessTerminal, final C creativeTerminal) {
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
			WT_TO_CREATIVE.put(wirelessTerminal, creativeTerminal);
			addNewRecipes(wirelessTerminal, creativeTerminal);
			getRegisteredTerminals().addAll(Lists.newArrayList(wirelessTerminal, creativeTerminal));
			AE2WTLib.PROXY.registerCustomRenderer(wirelessTerminal);
			AE2WTLib.PROXY.registerCustomRenderer(creativeTerminal);
		}
	}

	@Override
	public ICustomWirelessTerminalItem getCreativeVersion(final ICustomWirelessTerminalItem nonCreativeTerminal) {
		if (nonCreativeTerminal.isCreative()) {
			return nonCreativeTerminal;
		}
		if (WT_TO_CREATIVE.containsKey(nonCreativeTerminal)) {
			return WT_TO_CREATIVE.get(nonCreativeTerminal);
		}
		return null;
	}

	@Override
	public ItemStack convertToCreative(final ItemStack wirelessTerminal) {
		if (wirelessTerminal.getItem() instanceof ICustomWirelessTerminalItem) {
			final ICustomWirelessTerminalItem handler = (ICustomWirelessTerminalItem) wirelessTerminal.getItem();
			if (handler instanceof Item && !handler.isCreative()) {
				final ICustomWirelessTerminalItem creativeHandler = getCreativeVersion(handler);
				if (creativeHandler instanceof Item) {
					final NBTTagCompound wtNBT = wirelessTerminal.serializeNBT();
					if (wtNBT.hasKey("id", NBT.TAG_STRING)) {
						final String regName = wtNBT.getString("id");
						if (((Item) handler).getRegistryName().toString().equals(regName)) {
							wtNBT.setString("id", ((Item) creativeHandler).getRegistryName().toString());
							return new ItemStack(wtNBT);
						}
					}
				}
			}
		}
		return wirelessTerminal;
	}

	private void addNewRecipes(final ICustomWirelessTerminalItem... wirelessTerminals) {
		for (final ICustomWirelessTerminalItem wirelessTerminal : wirelessTerminals) {
			if (wirelessTerminal.isCreative()) {
				return;
			}
			final Item item = (Item) wirelessTerminal;
			final ItemStack stack = new ItemStack(item);
			for (final ICustomWirelessTerminalItem wth : getRegisteredTerminals()) {
				RecipeNewTerminal.addRecipe(stack, new ItemStack((Item) wth));
			}
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
