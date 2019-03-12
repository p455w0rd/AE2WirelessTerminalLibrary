package p455w0rd.ae2wtlib.api.client.gui.widgets;

import static p455w0rd.ae2wtlib.api.WTApi.Constants.NBT.AUTOCONSUME_BOOSTER_NBT;

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
	int iconIndex = 0;

	public GuiImgButtonBooster(final int x, final int y, ItemStack wirelessTerminal) {
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
	public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, float partial) {
		if (visible) {
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			mc.renderEngine.bindTexture(new ResourceLocation(wirelessTerminal.getItem().getRegistryName().getResourceDomain(), "textures/gui/states.png"));
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
		if (!getWirelessTerminal().getTagCompound().hasKey(AUTOCONSUME_BOOSTER_NBT)) {
			setValue(currentValue);
		}
		currentValue = getWirelessTerminal().getTagCompound().getBoolean(AUTOCONSUME_BOOSTER_NBT);
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
		getWirelessTerminal().getTagCompound().setBoolean(AUTOCONSUME_BOOSTER_NBT, value);
		WTApi.instance().getNetHandler().sendToServer(WTApi.instance().getNetHandler().createAutoConsumeBoosterPacket(value));
	}

}
