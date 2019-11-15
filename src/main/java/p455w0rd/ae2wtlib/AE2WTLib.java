package p455w0rd.ae2wtlib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.init.LibGlobals;
import p455w0rd.ae2wtlib.proxy.CommonProxy;

/**
 * @author p455w0rd
 *
 */
@Mod(modid = WTApi.MODID, name = LibGlobals.NAME, version = WTApi.VERSION, dependencies = WTApi.BASE_DEPS, acceptedMinecraftVersions = "[1.12.2]", certificateFingerprint = "@FINGERPRINT@")
public class AE2WTLib {

	@SidedProxy(clientSide = LibGlobals.CLIENT_PROXY, serverSide = LibGlobals.SERVER_PROXY)
	public static CommonProxy PROXY;

	@Instance(WTApi.MODID)
	public static AE2WTLib INSTANCE;

	@EventHandler
	public void preInit(final FMLPreInitializationEvent e) {
		INSTANCE = this;
		PROXY.preInit(e);
	}

	@EventHandler
	public void init(final FMLInitializationEvent e) {
		PROXY.init(e);
	}

	@EventHandler
	public void postInit(final FMLPostInitializationEvent e) {
		PROXY.postInit(e);
	}

}
