package p455w0rd.ae2wtlib.init;

import net.minecraftforge.fml.common.LoaderState;
import p455w0rd.ae2wtlib.AE2WTLib;
import p455w0rd.ae2wtlib.api.WTApi;

/**
 * @author p455w0rd
 *
 */
public class LibApiImpl extends WTApi {

	private static LibApiImpl INSTANCE = null;

	public static LibApiImpl instance() {
		if (LibApiImpl.INSTANCE == null) {
			if (!LibApiImpl.hasFinishedPreInit()) {
				return null;
			}
			LibApiImpl.INSTANCE = new LibApiImpl();
		}
		return INSTANCE;
	}

	protected static boolean hasFinishedPreInit() {
		if (AE2WTLib.PROXY.getLoaderState() == LoaderState.NOINIT) {
			//ModLogger.warn("API is not available until WCT finishes the PreInit phase.");
			return false;
		}

		return true;
	}

	@Override
	public boolean isInfinityBoosterCardEnabled() {
		return LibConfig.WT_BOOSTER_ENABLED;
	}

	@Override
	public boolean isOldInfinityMechanicEnabled() {
		return LibConfig.USE_OLD_INFINTY_MECHANIC;
	}

}