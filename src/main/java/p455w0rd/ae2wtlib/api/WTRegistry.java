package p455w0rd.ae2wtlib.api;

import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

/**
 * @author p455w0rd
 *
 */
public abstract class WTRegistry {

	public abstract List<? extends ICustomWirelessTerminalItem> getRegisteredTerminals();

	public abstract <T extends ICustomWirelessTerminalItem, C extends T> void registerWirelessTerminal(T wirelessTerminal, C creativeTerminal);

	public abstract ICustomWirelessTerminalItem getCreativeVersion(ICustomWirelessTerminalItem nonCreativeTerminal);

	public abstract ItemStack convertToCreative(ItemStack wirelessTerminal);

	public abstract Map<ICustomWirelessTerminalItem, ICustomWirelessTerminalItem> getNonCreativeToCreativeMap();

}
