package p455w0rd.ae2wtlib.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.config.GuiUtils;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.items.ItemWUT;

/**
 * @author p455w0rd
 *
 */
public class GuiWUTTermSelection extends GuiScreen {// GuiContainer {

	private final ResourceLocation[] textures;
	private int timeIn = 0;
	private int slotSelected = -1;
	private final ItemStack wut;
	final List<Pair<ItemStack, Integer>> installedTerminals;

	public GuiWUTTermSelection(final ItemStack stack) {
		//super(new ContainerWUTTerminalSelect());
		mc = Minecraft.getMinecraft();
		wut = stack;
		installedTerminals = ItemWUT.getStoredTerminalStacks(getTerminal());
		textures = WTApi.instance().getWUTUtility().getMenuIcons(stack);
	}

	private int getSegments() {
		return textures.length;
	}

	public ItemStack getTerminal() {
		return wut;
	}

	@Override
	public void initGui() {

		final ItemStack selectedTerminal = ItemWUT.getSelectedTerminalStack(getTerminal()).getLeft();
		for (int i = 0; i < installedTerminals.size(); i++) {
			addButton(new GuiMenuButton(installedTerminals.get(i).getRight(), this, selectedTerminal.getDisplayName()));
		}
		updateButtons(getTerminal());
	}

	private void updateButtons(final ItemStack tool) {
		int x = 0;
		for (int i = 0; i < buttonList.size(); i++) {
			final GuiMenuButton button = (GuiMenuButton) buttonList.get(i);
			if (!button.visible) {
				continue;
			}
			final int len = mc.fontRenderer.getStringWidth(button.displayString) + 6;
			x += len + 10;
			button.width = len;
			button.height = mc.fontRenderer.FONT_HEIGHT + 3;
			button.y = height / 2 - 110;
		}
		x = width / 2 - (x - 10) / 2;
		for (final GuiButton button : buttonList) {
			if (!button.visible) {
				continue;
			}
			button.x = x;
			x += button.width + 10;
		}
	}

	@Override
	public void drawScreen(final int mx, final int my, final float partialTicks) {
		ItemStack hoveredStack = ItemStack.EMPTY;
		final float stime = 5F;
		final float fract = Math.min(stime, timeIn + partialTicks) / stime;
		final int x = width / 2;
		final int y = height / 2;
		GlStateManager.pushMatrix();
		GlStateManager.translate((1 - fract) * x, (1 - fract) * y, 0);
		GlStateManager.scale(fract, fract, fract);
		super.drawScreen(mx, my, partialTicks);
		GlStateManager.popMatrix();
		if (getSegments() == 0) {
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();

		final int maxRadius = 80;

		final float angle = mouseAngle(x, y, mx, my);

		final int highlight = 5;

		GlStateManager.enableBlend();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		float totalDeg = 0;
		final float degPer = 360F / getSegments();

		final List<int[]> stringPositions = new ArrayList<>();

		final ItemStack tool = wut;
		if (tool.isEmpty()) {
			return;
		}

		slotSelected = -1;
		final float offset = 8.5F;
		final double dist = new Vec3d(x, y, 0).distanceTo(new Vec3d(mx, my, 0));
		final boolean inRange = dist > 35 && dist < 81;
		ResourceLocation[] signs;
		int modeIndex;
		modeIndex = ItemWUT.getSelectedTerminalStack(tool).getRight();
		signs = textures;

		for (int seg = 0; seg < getSegments(); seg++) {
			final boolean mouseInSector = inRange && angle > totalDeg && angle < totalDeg + degPer;
			float radius = Math.max(0F, Math.min((timeIn + partialTicks - seg * 6F / getSegments()) * 40F, maxRadius));

			GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

			float gs = 0.25F;
			if (seg % 2 == 0) {
				gs += 0.1F;
			}
			float r = gs;
			float b = gs + (seg == modeIndex ? 0.85F : 0.0F);
			float g = gs + (seg == modeIndex ? 0.5F : 0.0F);

			Color c = Color.decode(((ICustomWirelessTerminalItem) installedTerminals.get(modeIndex).getLeft().getItem()).getColor() + "");
			if (seg == modeIndex) {
				r = (float) c.getRed() / (float) 255;
				g = (float) c.getGreen() / (float) 255;
				b = (float) c.getBlue() / (float) 255;
			}
			final float a = 0.4F;
			if (mouseInSector) {
				hoveredStack = installedTerminals.get(seg).getLeft();
				if (seg != modeIndex) {
					if (Mouse.isButtonDown(0)) {
						((GuiMenuButton) buttonList.get(seg)).click();
					}
					slotSelected = seg;
					c = Color.decode(((ICustomWirelessTerminalItem) hoveredStack.getItem()).getColor() + "");
					r = (float) c.getRed() / (float) 255;
					g = (float) c.getGreen() / (float) 255;
					b = (float) c.getBlue() / (float) 255;
					//r = g = b = 1F;
				}
			}

			GlStateManager.color(r, g, b, a);

			for (float i = degPer; i >= 0; i--) {
				final float rad = (float) ((i + totalDeg) / 180F * Math.PI);
				final double xp = x + Math.cos(rad) * radius;
				final double yp = y + Math.sin(rad) * radius;
				if ((int) i == (int) (degPer / 2)) {
					stringPositions.add(new int[] {
							(int) xp, (int) yp, mouseInSector ? 'n' : 'r'
					});
				}

				GL11.glVertex2d(x + Math.cos(rad) * radius / 2.3F, y + Math.sin(rad) * radius / 2.3F);
				GL11.glVertex2d(xp, yp);
			}
			totalDeg += degPer;

			GL11.glEnd();

			if (mouseInSector) {
				radius -= highlight;
			}
		}
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.enableTexture2D();

		for (int i = 0; i < stringPositions.size(); i++) {
			final int[] pos = stringPositions.get(i);
			final int xp = pos[0];
			final int yp = pos[1];
			//final char c = (char) pos[2];

			final String name = "x";//"\u00a7" + c + tool.getDisplayName();

			int xsp = xp - 4;
			int ysp = yp;
			final int width = fontRenderer.getStringWidth(name);

			double mod = 0.6;
			int xdp = (int) ((xp - x) * mod + x);
			int ydp = (int) ((yp - y) * mod + y);

			if (xsp < x) {
				xsp -= width - 8;
			}
			if (ysp < y) {
				ysp -= 9;
			}

			fontRenderer.drawStringWithShadow("", xsp, ysp, i == modeIndex ? Color.GREEN.getRGB() : Color.WHITE.getRGB());

			mod = 0.7;
			xdp = (int) ((xp - x) * mod + x) - 7;
			ydp = (int) ((yp - y) * mod + y) - 10;

			mc.renderEngine.bindTexture(signs[i]);
			GlStateManager.color(1, 1, 1, 1);
			drawModalRectWithCustomSizedTexture(xdp - 8, ydp - 8, 0, 0, 32, 32, 32, 32);

		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		RenderHelper.enableGUIStandardItemLighting();

		final float s = 3F * fract;
		GlStateManager.scale(s, s, s);
		GlStateManager.translate(x / s - offset, y / s - 8, 0);
		mc.getRenderItem().renderItemAndEffectIntoGUI(tool, 0, 0);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableBlend();
		GlStateManager.disableRescaleNormal();

		GlStateManager.popMatrix();
		if (!hoveredStack.isEmpty()) {
			drawHoveringText(hoveredStack.getDisplayName(), mx, my, ((ICustomWirelessTerminalItem) hoveredStack.getItem()).getColor());
		}
	}

	public void drawHoveringText(final String text, final int mouseX, final int mouseY, final int color) {
		if (text != null && !text.isEmpty()) {
			//RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
			//if (MinecraftForge.EVENT_BUS.post(event)) {
			//    return;
			//}
			//mouseX = event.getX();
			//mouseY = event.getY();
			final int screenWidth = width;
			final int screenHeight = height;
			//final int maxTextWidth = -1;
			List<String> textList = Lists.newArrayList(text.split("\n"));
			final FontRenderer font = mc.fontRenderer;

			GlStateManager.disableRescaleNormal();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int tooltipTextWidth = 0;

			for (final String textItem : textList) {
				final int textLineWidth = font.getStringWidth(textItem);
				if (textLineWidth > tooltipTextWidth) {
					tooltipTextWidth = textLineWidth;
				}
			}

			boolean needsWrap = false;

			int titleLinesCount = 1;
			int tooltipX = mouseX + 12;
			if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
				tooltipX = mouseX - 16 - tooltipTextWidth;
				if (tooltipX < 4) // if the tooltip doesn't fit on the screen
				{
					if (mouseX > screenWidth / 2) {
						tooltipTextWidth = mouseX - 12 - 8;
					}
					else {
						tooltipTextWidth = screenWidth - 16 - mouseX;
					}
					needsWrap = true;
				}
			}

			if (needsWrap) {
				int wrappedTooltipWidth = 0;
				final List<String> wrappedTextLines = new ArrayList<>();
				for (int i = 0; i < textList.size(); i++) {
					final String textLine = textList.get(i);
					final List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
					if (i == 0) {
						titleLinesCount = wrappedLine.size();
					}

					for (final String line : wrappedLine) {
						final int lineWidth = font.getStringWidth(line);
						if (lineWidth > wrappedTooltipWidth) {
							wrappedTooltipWidth = lineWidth;
						}
						wrappedTextLines.add(line);
					}
				}
				tooltipTextWidth = wrappedTooltipWidth;
				textList = wrappedTextLines;

				if (mouseX > screenWidth / 2) {
					tooltipX = mouseX - 16 - tooltipTextWidth;
				}
				else {
					tooltipX = mouseX + 12;
				}
			}

			int tooltipY = mouseY - 12;
			int tooltipHeight = 8;

			if (textList.size() > 1) {
				tooltipHeight += (textList.size() - 1) * 10;
				if (textList.size() > titleLinesCount) {
					tooltipHeight += 2; // gap between title lines and next lines
				}
			}

			if (tooltipY < 4) {
				tooltipY = 4;
			}
			else if (tooltipY + tooltipHeight + 4 > screenHeight) {
				tooltipY = screenHeight - tooltipHeight - 4;
			}

			final int zLevel = 300;
			final int backgroundColor = 0xF0100010;
			final int borderColorStart = color;//0x505000FF;
			final int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
			//RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, textLines, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd);
			//MinecraftForge.EVENT_BUS.post(colorEvent);
			//backgroundColor = colorEvent.getBackground();
			//borderColorStart = colorEvent.getBorderStart();
			//borderColorEnd = colorEvent.getBorderEnd();
			GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
			GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
			GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			GuiUtils.drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
			GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
			GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
			GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

			// MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));
			//final int tooltipTop = tooltipY;

			for (int lineNumber = 0; lineNumber < textList.size(); ++lineNumber) {
				final String line = textList.get(lineNumber);
				font.drawStringWithShadow(line, tooltipX, tooltipY, -1);

				if (lineNumber + 1 == titleLinesCount) {
					tooltipY += 2;
				}

				tooltipY += 10;
			}

			//MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.enableRescaleNormal();
		}
	}

	private void changeMode() {
		if (slotSelected >= 0) {
		}
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		changeMode();
	}

	@Override
	public void updateScreen() {
		final ImmutableSet<KeyBinding> set = ImmutableSet.of(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindSneak, mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindJump);
		for (final KeyBinding k : set) {
			KeyBinding.setKeyBindState(k.getKeyCode(), GameSettings.isKeyDown(k));
		}
		timeIn++;
		//final ItemStack tool = wut;
		//final boolean changed = false;
		//if (changed) {
		//	updateButtons(tool);
		//}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private static float mouseAngle(final int x, final int y, final int mx, final int my) {
		final Vector2f baseVec = new Vector2f(1F, 0F);
		final Vector2f mouseVec = new Vector2f(mx - x, my - y);

		final float ang = (float) (Math.acos(Vector2f.dot(baseVec, mouseVec) / (baseVec.length() * mouseVec.length())) * (180F / Math.PI));
		return my < y ? 360F - ang : ang;
	}

}