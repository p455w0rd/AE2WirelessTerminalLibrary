/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package p455w0rd.ae2wtlib.api.client.gui.widgets;

import appeng.client.gui.widgets.ITooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiTabButton extends GuiButton implements ITooltip {
	private final RenderItem itemRenderer;
	private final String message;
	private int hideEdge = 0;
	private int myIcon = -1;
	private ItemStack myItem;

	public GuiTabButton(final int x, final int y, final int ico, final String message, final RenderItem ir) {
		super(0, 0, 16, "");

		this.x = x;
		this.y = y;
		width = 22;
		height = 22;
		myIcon = ico;
		this.message = message;
		itemRenderer = ir;
	}

	/**
	 * Using itemstack as an icon
	 *
	 * @param x x pos of button
	 * @param y y pos of button
	 * @param ico used icon
	 * @param message mouse over message
	 * @param ir renderer
	 */
	public GuiTabButton(final int x, final int y, final ItemStack ico, final String message, final RenderItem ir) {
		super(0, 0, 16, "");
		this.x = x;
		this.y = y;
		width = 22;
		height = 22;
		myItem = ico;
		this.message = message;
		itemRenderer = ir;
	}

	@Override
	public void drawButton(final Minecraft minecraft, final int x, final int y, float partial) {
		if (visible) {
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			minecraft.renderEngine.bindTexture(new ResourceLocation("appliedenergistics2", "textures/guis/states.png"));
			hovered = x >= this.x && y >= this.y && x < this.x + width && y < this.y + height;

			int uv_x = (hideEdge > 0 ? 11 : 13);

			final int offsetX = hideEdge > 0 ? 1 : 0;

			this.drawTexturedModalRect(this.x, this.y, uv_x * 16, 0, 25, 22);

			if (myIcon >= 0) {
				final int uv_y = (int) Math.floor(myIcon / 16);
				uv_x = myIcon - uv_y * 16;

				this.drawTexturedModalRect(offsetX + this.x + 3, this.y + 3, uv_x * 16, uv_y * 16, 16, 16);
			}

			mouseDragged(minecraft, x, y);

			if (myItem != null) {
				zLevel = 100.0F;
				itemRenderer.zLevel = 100.0F;

				GlStateManager.enableDepth();
				RenderHelper.enableGUIStandardItemLighting();
				itemRenderer.renderItemAndEffectIntoGUI(myItem, offsetX + this.x + 3, this.y + 3);
				GlStateManager.disableDepth();

				itemRenderer.zLevel = 0.0F;
				zLevel = 0.0F;
			}
		}
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public int xPos() {
		return x;
	}

	@Override
	public int yPos() {
		return y;
	}

	@Override
	public int getWidth() {
		return 22;
	}

	@Override
	public int getHeight() {
		return 22;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public int getHideEdge() {
		return hideEdge;
	}

	public void setHideEdge(final int hideEdge) {
		this.hideEdge = hideEdge;
	}
}
