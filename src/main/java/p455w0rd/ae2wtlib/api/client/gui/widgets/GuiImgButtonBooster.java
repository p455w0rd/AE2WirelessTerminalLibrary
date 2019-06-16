package p455w0rd.ae2wtlib.api.client.gui.widgets;

import appeng.client.gui.widgets.ITooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import p455w0rd.ae2wtlib.api.WTApi;

/**
 * @author p455w0rd
 *
 */
public class GuiImgButtonBooster extends GuiButton implements ITooltip {

	private ItemStack wirelessTerminal = ItemStack.EMPTY;
	private boolean currentValue = false;

	public GuiImgButtonBooster(final int x, final int y, final ItemStack wirelessTerminal) {
		super(0, x, y, "");
		this.x = x;
		this.y = y;
		width = 16;
		height = 16;
		this.wirelessTerminal = wirelessTerminal;
	}

	public void setVisibility(final boolean vis) {
		visible = vis;
		enabled = vis;
	}

	@Override
	public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partial) {
		if (visible) {
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			mc.renderEngine.bindTexture(new ResourceLocation(WTApi.instance().getConstants().getModId(), "textures/gui/states.png"));
			this.drawTexturedModalRect(x, y, 0, 0, 16, 16);
			this.drawTexturedModalRect(x, y, (!getCurrentValue() ? 3 : 2) * 16, 0, 16, 16);
			mouseDragged(mc, mouseX, mouseY);
		}
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public boolean getCurrentValue() {
		if (!getWirelessTerminal().hasTagCompound()) {
			getWirelessTerminal().setTagCompound(new NBTTagCompound());
		}
		if (!getWirelessTerminal().getTagCompound().hasKey(WTApi.instance().getConstants().getNBTTagNames().autoConsumeBooster())) {
			setValue(currentValue);
		}
		currentValue = getWirelessTerminal().getTagCompound().getBoolean(WTApi.instance().getConstants().getNBTTagNames().autoConsumeBooster());
		return currentValue;
	}

	@Override
	public String getMessage() {
		return I18n.format("gui.consumeboosters") + "\n" + TextFormatting.GRAY + (!getCurrentValue() ? I18n.format("gui.dontconsumeboosters.desc") : I18n.format("gui.consumeboosters.desc") + "\n" + I18n.format("gui.consumeboosters.desc2"));
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
		return wirelessTerminal;
	}

	public void cycleValue() {
		setValue(!getCurrentValue());
	}

	private void setValue(final boolean value) {
		if (!getWirelessTerminal().hasTagCompound()) {
			getWirelessTerminal().setTagCompound(new NBTTagCompound());
		}
		getWirelessTerminal().getTagCompound().setBoolean(WTApi.instance().getConstants().getNBTTagNames().autoConsumeBooster(), value);
		WTApi.instance().getNetHandler().sendToServer(WTApi.instance().getNetHandler().createAutoConsumeBoosterPacket(value));
	}

}
