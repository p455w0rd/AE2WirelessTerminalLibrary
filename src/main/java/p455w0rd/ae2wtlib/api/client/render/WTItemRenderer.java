package p455w0rd.ae2wtlib.api.client.render;

import java.util.*;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.LightUtil;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rdslib.api.client.*;

/**
 * @author p455w0rd
 *
 */
public class WTItemRenderer extends TileEntityItemStackRenderer implements ICustomItemRenderer {

	public ItemLayerWrapper model;
	public static TransformType transformType;
	private static final Map<Item, ICustomItemRenderer> CACHE = new HashMap<>();

	private WTItemRenderer(@Nonnull final Item item) {
		registerRenderer(item, this);
	}

	private static void registerRenderer(final Item item, final ICustomItemRenderer instance) {
		CACHE.put(item, instance);
	}

	public static ICustomItemRenderer getRendererForItem(final Item item) {
		if (!CACHE.containsKey(item)) {
			new WTItemRenderer(item);
		}
		return CACHE.get(item);
	}

	@Override
	public void renderByItem(final ItemStack stack, final float partialTicks) {
		if (stack == null) {
			return;
		}
		if (model == null) {
			final IBakedModel baseModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
			if (baseModel == null) {
				return;
			}
			final ItemLayerWrapper wrapper = new ItemLayerWrapper(baseModel).setRenderer(this);
			final Item item = stack.getItem();
			if (item instanceof IModelHolder) {
				((IModelHolder) item).setWrappedModel(wrapper);
			}
			model = wrapper;
		}
		final float pbx = OpenGlHelper.lastBrightnessX;
		final float pby = OpenGlHelper.lastBrightnessY;
		if (stack.getItem().hasEffect(stack)) {
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		}
		RenderModel.render(model, stack);
		if (stack.hasEffect()) {
			GlintEffectRenderer.apply(model, ((ICustomWirelessTerminalItem) stack.getItem()).getColor());
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, pbx, pby);
		}
	}

	/*
	@Override
	public WTItemRenderer setModel(final ItemLayerWrapper wrappedModel) {
		model = wrappedModel;
		model.setRenderer(this);
		return this;
	}
	*/
	public static class RenderModel {
		public static void render(final IBakedModel model, @Nonnull final ItemStack stack) {
			render(model, -1, stack);
		}

		public static void render(final IBakedModel model, final int color) {
			render(model, color, ItemStack.EMPTY);
		}

		public static void render(final IBakedModel model, final int color, @Nonnull final ItemStack stack) {
			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
			for (final EnumFacing enumfacing : EnumFacing.values()) {
				renderQuads(vertexbuffer, model.getQuads((IBlockState) null, enumfacing, 0L), color, stack);
			}
			//List<BakedQuad> tmpQuads = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack).getQuads(null, null, 0);
			renderQuads(vertexbuffer, model.getQuads((IBlockState) null, (EnumFacing) null, 0L), color, stack);
			//renderQuads(vertexbuffer, tmpQuads, color, stack);
			tessellator.draw();
		}

		public static void renderQuads(final BufferBuilder renderer, final List<BakedQuad> quads, final int color, final ItemStack stack) {
			final boolean flag = color == -1 && !stack.isEmpty();
			int i = 0;
			for (final int j = quads.size(); i < j; i++) {
				final BakedQuad bakedquad = quads.get(i);
				int k = color;
				if (flag && bakedquad.hasTintIndex()) {
					final ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
					k = itemColors.colorMultiplier(stack, bakedquad.getTintIndex());
					if (EntityRenderer.anaglyphEnable) {
						k = TextureUtil.anaglyphColor(k);
					}
					k |= 0xFF000000;
				}
				LightUtil.renderQuadColor(renderer, bakedquad, k);
			}
		}
	}

	public static class GlintEffectRenderer {

		public static void apply(final IBakedModel model, final int color) {
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.depthMask(false);
			GlStateManager.depthFunc(514);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(SourceFactor.SRC_COLOR, DestFactor.ONE);
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("minecraft", "textures/misc/enchanted_item_glint.png"));
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(8.0F, 8.0F, 8.0F);
			final float f = Minecraft.getSystemTime() % 3000L / 3000.0F / 8.0F;
			GlStateManager.translate(f, 0.0F, 0.0F);
			GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
			RenderModel.render(model, color);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableLighting();
			GlStateManager.depthFunc(515);
			GlStateManager.depthMask(true);
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		}

	}

	@Override
	public TransformType getTransformType() {
		return transformType;
	}

	@Override
	public void setTransformType(final TransformType type) {
		transformType = type;
	}

}
