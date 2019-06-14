package p455w0rd.ae2wtlib.api.client.gui.widgets;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import p455w0rd.ae2wtlib.api.client.IWTGuiScrollbar;
import p455w0rd.ae2wtlib.api.client.gui.GuiWT;
import p455w0rd.ae2wtlib.init.LibGlobals;

public class GuiScrollbar implements IWTGuiScrollbar {

	private int displayX = 0;
	private int displayY = 0;
	private int width = 12;
	private int height = 16;
	private int pageSize = 1;

	private int maxScroll = 0;
	private int minScroll = 0;
	private int currentScroll = 0;

	@Override
	public void draw(final GuiWT g) {
		g.mc.renderEngine.bindTexture(new ResourceLocation(LibGlobals.MODID, "textures/gui/states.png"));
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		if (getRange() == 0) {
			g.drawTexturedModalRect(displayX, displayY, 18 + width, 241, width, 15);
		}
		else {
			final int offset = (currentScroll - minScroll) * (height - 15) / getRange();
			g.drawTexturedModalRect(displayX, offset + displayY, 18, 241, width, 15);
		}
	}

	private int getRange() {
		return maxScroll - minScroll;
	}

	public int getLeft() {
		return displayX;
	}

	@Override
	public GuiScrollbar setLeft(final int v) {
		displayX = v;
		return this;
	}

	public int getTop() {
		return displayY;
	}

	@Override
	public GuiScrollbar setTop(final int v) {
		displayY = v;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public GuiScrollbar setWidth(final int v) {
		width = v;
		return this;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public GuiScrollbar setHeight(final int v) {
		height = v;
		return this;
	}

	@Override
	public void setRange(final int min, final int max, final int pageSize) {
		minScroll = min;
		maxScroll = max;
		this.pageSize = pageSize;

		if (minScroll > maxScroll) {
			maxScroll = minScroll;
		}

		applyRange();
	}

	private void applyRange() {
		currentScroll = Math.max(Math.min(currentScroll, maxScroll), minScroll);
	}

	@Override
	public int getCurrentScroll() {
		return currentScroll;
	}

	@Override
	public void click(final GuiWT aeBaseGui, final int x, final int y) {
		if (getRange() == 0) {
			return;
		}

		if (x > displayX && x <= displayX + width) {
			if (y > displayY && y <= displayY + height) {
				currentScroll = y - displayY;
				currentScroll = minScroll + currentScroll * 2 * getRange() / height;
				currentScroll = currentScroll + 1 >> 1;
				applyRange();
			}
		}
	}

	@Override
	public void wheel(int delta) {
		delta = Math.max(Math.min(-delta, 1), -1);
		currentScroll += delta * pageSize;
		applyRange();
	}
}