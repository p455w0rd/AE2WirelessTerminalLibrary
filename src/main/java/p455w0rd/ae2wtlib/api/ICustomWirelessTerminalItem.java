package p455w0rd.ae2wtlib.api;

import net.minecraft.item.ItemStack;

/**
 * @author p455w0rd
 *
 */
public interface ICustomWirelessTerminalItem extends ICustomWirelessTermHandler {

	// checks if an Infinity Booster Card is installed on the WT
	public default boolean checkForBooster(final ItemStack wirelessTerminal) {
		return WTApi.instance().getConfig().isOldInfinityMechanicEnabled() ? WTApi.instance().isBoosterInstalled(wirelessTerminal) : (WTApi.instance().hasInfiniteRange(wirelessTerminal) && WTApi.instance().hasInfinityEnergy(wirelessTerminal));
	}

	// checks if the Terminal is a creative version
	public default boolean isCreative() {
		return false;
	}

}
