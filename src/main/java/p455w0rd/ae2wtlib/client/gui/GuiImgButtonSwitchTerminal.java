package p455w0rd.ae2wtlib.client.gui;

import org.lwjgl.opengl.GL11;

import appeng.client.gui.widgets.ITooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.items.ItemWUT;

/**
 * @author p455w0rd
 *
 */
public class GuiImgButtonSwitchTerminal extends GuiButton implements ITooltip {

	final ItemStack wut;
	final ItemStack nextStack;
	final ItemStack previousStack;

	public GuiImgButtonSwitchTerminal(final int x, final int y, final ItemStack wut) {
		super(0, x, y, 16, 16, "");
		this.wut = wut;
		previousStack = ItemWUT.getStoredTerminalByIndex(wut, ItemWUT.getNextIndex(wut));
		nextStack = ItemWUT.getStoredTerminalByIndex(wut, ItemWUT.getPreviousIndex(wut));
	}

	@Override
	public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partial) {
		if (visible) {
			//final GuiContainer gui = (GuiContainer) mc.currentScreen;
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			mc.renderEngine.bindTexture(new ResourceLocation(WTApi.MODID, "textures/gui/states.png"));
			this.drawTexturedModalRect(x, y, 0, 0, 16, 16);
			if (!ItemStack.areItemStacksEqual(previousStack, nextStack)) {
				GlStateManager.scale(0.5, 0.5, 0.5);
				GlStateManager.translate(x + 1, y + 7, 0);
			}
			mc.getRenderItem().renderItemAndEffectIntoGUI(previousStack, x, y);
			if (!ItemStack.areItemStacksEqual(previousStack, nextStack)) {
				GlStateManager.translate(-x * 2 - 0.5, -y * 4 - 50, 0);
				GlStateManager.translate(x * 2 + 16, y * 4 + 50, 0);
				mc.getRenderItem().renderItemAndEffectIntoGUI(nextStack, x, y);
				GlStateManager.translate(-x - 18, -y - 7, 0);
				GlStateManager.scale(2.0058, 2, 2);
				GlStateManager.enableAlpha();
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				mc.getTextureManager().bindTexture(new ResourceLocation(WTApi.MODID, "textures/gui/states.png"));
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(GL11.GL_BLEND);
				GlStateManager.translate(0.42F, 0, 0);
				this.drawTexturedModalRect(x, y, 5 * 16, 0, 16, 16);
				GlStateManager.translate(-0.5, 0, 0);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glPopAttrib();
			}
			mouseDragged(mc, mouseX, mouseY);
		}
	}

	@Override
	public String getMessage() {
		String message = nextStack.getDisplayName();
		if (!ItemStack.areItemStacksEqual(previousStack, nextStack)) {
			message = "Left Click: " + message + "\nRight Click: " + previousStack.getDisplayName();
			message = "======== Switch Terminals ========\n" + message;
			return message;
		}
		message = "Switch to " + message;
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
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public ItemStack getWirelessTerminal() {
		return wut;
	}

}
