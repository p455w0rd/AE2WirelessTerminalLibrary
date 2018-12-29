package p455w0rd.ae2wtlib.api.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import p455w0rd.ae2wtlib.client.render.ItemLayerWrapper;

public interface IModelHolder {

	void initModel();

	//used for TEISR rendering
	default ItemLayerWrapper getWrappedModel() {
		return null;
	}

	//used for TEISR rendering
	default void setWrappedModel(ItemLayerWrapper wrappedModel) {
	}

	//used for TEISR rendering
	default boolean shouldUseInternalTEISR() {
		return false;
	}

	//used for TEISR rendering (in situations where the item model uses a different registry name than the item)
	ModelResourceLocation getModelResource();

}