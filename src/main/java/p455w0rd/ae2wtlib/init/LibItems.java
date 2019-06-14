package p455w0rd.ae2wtlib.init;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.items.ItemInfinityBooster;
import p455w0rd.ae2wtlib.items.ItemWUT;
import p455w0rdslib.api.client.ItemRenderingRegistry;

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

	private static final List<Item> ITEM_LIST = Lists.newArrayList(ITEM_ARRAY);

	public static final List<Item> getList() {
		return ITEM_LIST;
	}

	public static final Item[] getArray() {
		return ITEM_ARRAY;
	}

	public static final void register(final RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(getArray());
		WTApi.instance().getWirelessTerminalRegistry().registerWirelessTerminal(ULTIMATE_TERMINAL);
		ItemRenderingRegistry.registerCustomRenderingItem(BOOSTER_CARD);
	}
	/*
	@SideOnly(Side.CLIENT)
	public static final void initModels(final ModelBakeEvent event) {
		for (final Item item : ITEM_ARRAY) {
			if (item instanceof IModelHolder) {
				initModel(event,item);
			}
		}
		for (final IModelHolder holder:WTApi.instance().getWirelessTerminalRegistry().getRegisteredTerminals()) {
			if (holder instanceof Item) {
				initModel(event,(Item)holder);
			}
		}
	}
	
	
	private static final void initModel(final ModelBakeEvent event, Item item) {
		final IModelHolder holder = (IModelHolder) item;
		holder.initModel();
		if (holder.shouldUseInternalTEISR()) {
			final IBakedModel currentModel = event.getModelRegistry().getObject(new ModelResourceLocation(item.getRegistryName(), "inventory"));
			holder.setWrappedModel(new ItemLayerWrapper(currentModel).setRenderer(WTItemRenderer.getRendererForItem(item)));
			event.getModelRegistry().putObject(new ModelResourceLocation(item.getRegistryName(), "inventory"), holder.getWrappedModel());
		}
	}

	@SideOnly(Side.CLIENT)
	public static final void registerTEISRs(final ModelRegistryEvent event) {
		for (final Item item : getList()) {
			if (item instanceof IModelHolder) {
				final IModelHolder holder = (IModelHolder) item;
				if (holder.shouldUseInternalTEISR()) {
					item.setTileEntityItemStackRenderer((TileEntityItemStackRenderer) WTItemRenderer.getRendererForItem(item));
				}
			}
		}
	}
	*/

}
