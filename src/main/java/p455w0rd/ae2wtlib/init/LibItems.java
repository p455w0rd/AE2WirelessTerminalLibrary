package p455w0rd.ae2wtlib.init;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import p455w0rd.ae2wtlib.api.client.IModelHolder;
import p455w0rd.ae2wtlib.client.render.ItemLayerWrapper;
import p455w0rd.ae2wtlib.client.render.WTItemRenderer;
import p455w0rd.ae2wtlib.items.ItemInfinityBooster;

/**
 * @author p455w0rd
 *
 */
public class LibItems {

	public static final ItemInfinityBooster BOOSTER_CARD = new ItemInfinityBooster();

	private static final Item[] ITEM_LIST = new Item[] {
			BOOSTER_CARD
	};

	public static final List<Item> getList() {
		return Lists.newArrayList(ITEM_LIST);
	}

	public static final Item[] getArray() {
		return ITEM_LIST;
	}

	public static final void register(IForgeRegistry<Item> registry) {
		registry.registerAll(getArray());
	}

	@SideOnly(Side.CLIENT)
	public static final void initModels(ModelBakeEvent event) {
		for (Item item : getList()) {
			if (item instanceof IModelHolder) {
				IModelHolder holder = (IModelHolder) item;
				holder.initModel();
				if (holder.shouldUseInternalTEISR()) {
					IBakedModel wtModel = event.getModelRegistry().getObject(new ModelResourceLocation(item.getRegistryName(), "inventory"));
					holder.setWrappedModel(new ItemLayerWrapper(wtModel));
					event.getModelRegistry().putObject(new ModelResourceLocation(item.getRegistryName(), "inventory"), holder.getWrappedModel());
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static final void registerTEISRs(ModelRegistryEvent event) {
		for (Item item : getList()) {
			if (item instanceof IModelHolder) {
				IModelHolder holder = (IModelHolder) item;
				if (holder.shouldUseInternalTEISR()) {
					item.setTileEntityItemStackRenderer(new WTItemRenderer().setModel(holder.getWrappedModel()));
				}
			}
		}
	}

}
