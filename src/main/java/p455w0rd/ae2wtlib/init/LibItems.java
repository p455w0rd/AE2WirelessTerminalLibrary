package p455w0rd.ae2wtlib.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import p455w0rd.ae2wtlib.AE2WTLib;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.items.ItemInfinityBooster;
import p455w0rd.ae2wtlib.items.ItemWUT;

/**
 * @author p455w0rd
 *
 */
public class LibItems {

	public static final ItemInfinityBooster BOOSTER_CARD = new ItemInfinityBooster();
	public static final ItemWUT ULTIMATE_TERMINAL = new ItemWUT();

	private static final Item[] ITEM_ARRAY = new Item[] {
			BOOSTER_CARD, ULTIMATE_TERMINAL
	};

	public static final void register(final RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(ITEM_ARRAY);
		WTApi.instance().getWirelessTerminalRegistry().registerWirelessTerminal(ULTIMATE_TERMINAL);
		AE2WTLib.PROXY.registerCustomRenderer(BOOSTER_CARD);
	}

}
