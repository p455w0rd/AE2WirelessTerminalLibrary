package p455w0rd.ae2wtlib.api.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IBaubleRender {

	/**
	 * Credit to EnderIO team, used from EnderIO
	 *
	 * A few helper methods for rendering. Credit to Vazkii, used from Botania.
	 * {@link #translateToHeadLevel(EntityPlayer)} edited to remove sneaking
	 * translation.
	 */
	public static class Helper {

		public static void rotateIfSneaking(EntityPlayer player) {
			if (player.isSneaking()) {
				applySneakingRotation();
			}
		}

		public static void applySneakingRotation() {
			GlStateManager.rotate(28.64789F, 1.0F, 0.0F, 0.0F);
		}

		public static void translateToHeadLevel(EntityPlayer player) {
			GlStateManager.translate(0, (player != Minecraft.getMinecraft().player ? 1.7F : 0) - player.getDefaultEyeHeight(), 0);
		}
	}

	void doRenderLayer(RenderPlayer playerRenderer, ItemStack piece, int slot, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale);

}
