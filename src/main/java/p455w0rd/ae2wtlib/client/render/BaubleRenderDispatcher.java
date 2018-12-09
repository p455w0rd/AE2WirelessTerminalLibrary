package p455w0rd.ae2wtlib.client.render;

import java.util.ArrayList;
import java.util.List;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import p455w0rd.ae2wtlib.api.IBaubleItem;
import p455w0rd.ae2wtlib.api.IBaubleRender;

/**
 * from EnderIO
 *
 */
public class BaubleRenderDispatcher implements LayerRenderer<AbstractClientPlayer> {

	public final static BaubleRenderDispatcher instance = new BaubleRenderDispatcher(null);

	private final RenderPlayer renderPlayer;

	public BaubleRenderDispatcher(RenderPlayer renderPlayer) {
		this.renderPlayer = renderPlayer;
	}

	private static final List<RenderPlayer> REGISTRY = new ArrayList<RenderPlayer>();

	public static final List<RenderPlayer> getRegistry() {
		return REGISTRY;
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(entitylivingbaseIn);
		if (baubles != null) {
			for (int i = 0; i < baubles.getSlots(); i++) {
				ItemStack piece = baubles.getStackInSlot(i);
				if (piece != null && piece.getItem() instanceof IBaubleItem) {
					IBaubleItem bauble = (IBaubleItem) piece.getItem();
					IBaubleRender render = bauble.getRender();
					if (render != null) {
						render.doRenderLayer(renderPlayer, piece, entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
					}
				}
			}
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return true;
	}

}