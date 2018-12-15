package p455w0rd.ae2wtlib.init;

import java.util.List;

import com.google.common.collect.Lists;

import appeng.api.AEApi;
import appeng.api.features.IWirelessTermHandler;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.LoaderState;
import p455w0rd.ae2wtlib.AE2WTLib;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.items.ItemWT;

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
	public void registerWirelessTerminal(ICustomWirelessTerminalItem wirelessTerminal) {
		if (AE2WTLib.PROXY.getLoaderState() != LoaderState.PREINITIALIZATION) {
			LibLogger.warn("Wireless Terminals must be registered during PreInit!");
			return;
		}
		if (wirelessTerminal != null) {
			if (!(wirelessTerminal instanceof ItemWT)) {
				LibLogger.warn("Wireless terminal items must extends ItemWT.class");
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
			getRegisteredTerminals().add(wirelessTerminal);
		}
	}

	private static void registerTerminalWithAE2(IWirelessTermHandler wirelessTerminal) {
		if (wirelessTerminal instanceof ItemWT) {
			AEApi.instance().registries().wireless().registerWirelessHandler(wirelessTerminal);
			AEApi.instance().registries().charger().addChargeRate((Item) wirelessTerminal, LibConfig.WT_MAX_POWER);
		}
	}

	public static void registerAllTerminalsWithAE2() {
		for (ICustomWirelessTerminalItem wirelessTerminal : WTApi.instance().getRegistry().getRegisteredTerminals()) {
			if (wirelessTerminal instanceof ItemWT) {
				registerTerminalWithAE2(wirelessTerminal);
			}
		}
	}

}
