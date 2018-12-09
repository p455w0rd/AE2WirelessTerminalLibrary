package p455w0rd.ae2wtlib.api;

import net.minecraft.item.ItemStack;
import p455w0rd.ae2wtlib.init.LibConfig;
import p455w0rd.ae2wtlib.util.WTUtils;

/**
 * @author p455w0rd
 *
 */
public interface ICustomWirelessTerminalItem extends ICustomWirelessTermHandler {

	// checks if an Infinity Booster Card is installed on the WT
	public default boolean checkForBooster(final ItemStack wirelessTerminal) {
		return LibConfig.USE_OLD_INFINTY_MECHANIC ? WTUtils.isBoosterInstalled(wirelessTerminal) : (WTUtils.hasInfiniteRange(wirelessTerminal) && WTUtils.hasInfinityEnergy(wirelessTerminal));
	}

	// checks if the Terminal is a creative version
	public default boolean isCreative() {
		return false;
	}

}
