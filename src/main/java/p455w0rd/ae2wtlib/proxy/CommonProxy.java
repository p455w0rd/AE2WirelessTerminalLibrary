package p455w0rd.ae2wtlib.proxy;

import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import p455w0rd.ae2wtlib.AE2WTLib;
import p455w0rd.ae2wtlib.init.LibConfig;
import p455w0rd.ae2wtlib.init.LibNetworking;
import p455w0rdslib.util.ChunkUtils;

/**
 * @author p455w0rd
 *
 */
public class CommonProxy {

	private static LoaderState WT_STATE = LoaderState.NOINIT;

	public void preInit(FMLPreInitializationEvent e) {
		WT_STATE = LoaderState.PREINITIALIZATION;
		LibConfig.preInit();
		LibNetworking.preInit();
		ChunkUtils.register(AE2WTLib.INSTANCE);
	}

	public void init(FMLInitializationEvent e) {
		WT_STATE = LoaderState.INITIALIZATION;
	}

	public void postInit(FMLPostInitializationEvent e) {
		WT_STATE = LoaderState.POSTINITIALIZATION;
		LibNetworking.postInit();
	}

	public LoaderState getLoaderState() {
		return WT_STATE;
	}

}
