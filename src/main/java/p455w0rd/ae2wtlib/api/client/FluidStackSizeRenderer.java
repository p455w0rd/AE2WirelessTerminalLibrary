package p455w0rd.ae2wtlib.api.client;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import appeng.api.storage.data.IAEFluidStack;
import appeng.core.AEConfig;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @author p455w0rd
 *
 */
public class FluidStackSizeRenderer extends StackSizeRenderer<IAEFluidStack> {

	private static final FluidStackSizeRenderer INSTANCE = new FluidStackSizeRenderer();

	public static final FluidStackSizeRenderer getInstance() {
		return INSTANCE;
	}

	private static final String[] NUMBER_FORMATS = new String[] {
			"#.000", "#.00", "#.0", "#"
	};

	@Override
	public void renderStackSize(final FontRenderer fontRenderer, final IAEFluidStack aeStack, final int xPos, final int yPos) {
		if (aeStack != null) {
			final float scaleFactor = AEConfig.instance().useTerminalUseLargeFont() ? 0.85f : 0.5f;
			final float inverseScaleFactor = 1.0f / scaleFactor;
			final int offset = AEConfig.instance().useTerminalUseLargeFont() ? 0 : -1;

			final boolean unicodeFlag = fontRenderer.getUnicodeFlag();
			fontRenderer.setUnicodeFlag(false);

			if (aeStack.getStackSize() > 0) {
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
		// Handle any value below 100 (large font) or 1000 (small font) Buckets with a custom formatter,
		// otherwise pass it to the normal number converter
		if (originalSize < 1000 * 100 && AEConfig.instance().useTerminalUseLargeFont()) {
			return getSlimRenderedStacksize(originalSize);
		}
		else if (originalSize < 1000 * 1000 && !AEConfig.instance().useTerminalUseLargeFont()) {
			return getWideRenderedStacksize(originalSize);
		}

		if (AEConfig.instance().useTerminalUseLargeFont()) {
			return getConverter().toSlimReadableForm(originalSize / 1000);
		}
		else {
			return getConverter().toWideReadableForm(originalSize / 1000);
		}
	}

	private String getSlimRenderedStacksize(final long originalSize) {
		final int log = 1 + (int) Math.floor(Math.log10(originalSize)) / 2;

		return getRenderedFluidStackSize(originalSize, log);
	}

	private String getWideRenderedStacksize(final long originalSize) {
		final int log = (int) Math.floor(Math.log10(originalSize)) / 2;

		return getRenderedFluidStackSize(originalSize, log);
	}

	private String getRenderedFluidStackSize(final long originalSize, final int log) {
		final int index = Math.max(0, Math.min(3, log));
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		final DecimalFormat format = new DecimalFormat(NUMBER_FORMATS[index]);
		format.setDecimalFormatSymbols(symbols);
		format.setRoundingMode(RoundingMode.DOWN);
		return format.format(originalSize / 1000D);
	}

}
