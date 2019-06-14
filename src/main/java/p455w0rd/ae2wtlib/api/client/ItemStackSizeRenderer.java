package p455w0rd.ae2wtlib.api.client;

import appeng.api.storage.data.IAEItemStack;
import appeng.core.AEConfig;
import appeng.core.localization.GuiText;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @author p455w0rd
 *
 */
public class ItemStackSizeRenderer extends StackSizeRenderer<IAEItemStack> {

	private static final ItemStackSizeRenderer INSTANCE = new ItemStackSizeRenderer();

	public static final ItemStackSizeRenderer getInstance() {
		return INSTANCE;
	}

	@Override
	public void renderStackSize(final FontRenderer fontRenderer, final IAEItemStack aeStack, final int xPos, final int yPos) {
		if (aeStack != null) {
			final float scaleFactor = AEConfig.instance().useTerminalUseLargeFont() ? 0.85f : 0.5f;
			final float inverseScaleFactor = 1.0f / scaleFactor;
			final int offset = AEConfig.instance().useTerminalUseLargeFont() ? 0 : -1;

			final boolean unicodeFlag = fontRenderer.getUnicodeFlag();
			fontRenderer.setUnicodeFlag(false);

			if ((aeStack.getStackSize() == 0 || GuiScreen.isAltKeyDown()) && aeStack.isCraftable()) {
				final String craftLabelText = AEConfig.instance().useTerminalUseLargeFont() ? GuiText.LargeFontCraft.getLocal() : GuiText.SmallFontCraft.getLocal();
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.disableBlend();
				GlStateManager.pushMatrix();
				GlStateManager.scale(scaleFactor, scaleFactor, scaleFactor);
				final int X = (int) (((float) xPos + offset + 16.0f - fontRenderer.getStringWidth(craftLabelText) * scaleFactor) * inverseScaleFactor);
				final int Y = (int) (((float) yPos + offset + 16.0f - 7.0f * scaleFactor) * inverseScaleFactor);
				fontRenderer.drawStringWithShadow(craftLabelText, X, Y, 16777215);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
				GlStateManager.enableBlend();
			}

			if (aeStack.getStackSize() > 0 && (!aeStack.isCraftable() || aeStack.isCraftable() && !GuiScreen.isAltKeyDown())) {
				final String stackSize = getToBeRenderedStackSize(aeStack.getStackSize());

				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.disableBlend();
				GlStateManager.pushMatrix();
				GlStateManager.scale(scaleFactor, scaleFactor, scaleFactor);
				final int X = (int) (((float) xPos + offset + 16.0f - fontRenderer.getStringWidth(stackSize) * scaleFactor) * inverseScaleFactor);
				final int Y = (int) (((float) yPos + offset + 16.0f - 7.0f * scaleFactor) * inverseScaleFactor);
				fontRenderer.drawStringWithShadow(stackSize, X, Y, 16777215);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
				GlStateManager.enableBlend();
			}

			fontRenderer.setUnicodeFlag(unicodeFlag);
		}
	}

	private String getToBeRenderedStackSize(final long originalSize) {
		if (AEConfig.instance().useTerminalUseLargeFont()) {
			return getConverter().toSlimReadableForm(originalSize);
		}
		else {
			return getConverter().toWideReadableForm(originalSize);
		}
	}
}
