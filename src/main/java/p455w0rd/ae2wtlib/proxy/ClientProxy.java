package p455w0rd.ae2wtlib.proxy;

import net.minecraftforge.fml.common.event.*;
import p455w0rdslib.api.client.IModelHolder;
import p455w0rdslib.api.client.ItemRenderingRegistry;

/**
 * @author p455w0rd
 *
 */
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(final FMLPreInitializationEvent e) {
		super.preInit(e);
		//LibCreativeTab.preInit();
	}

	@Override
	public void init(final FMLInitializationEvent e) {
		super.init(e);
	}

	@Override
	public void postInit(final FMLPostInitializationEvent e) {
		super.postInit(e);
	}

	@Override
	public void registerCustomRenderer(final IModelHolder wirelessTerminal) {
		ItemRenderingRegistry.registerCustomRenderingItem(wirelessTerminal);
	}

}
