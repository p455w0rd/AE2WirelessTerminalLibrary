package p455w0rd.ae2wtlib.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.init.LibNetworking;
import p455w0rd.ae2wtlib.items.ItemWUT;
import p455w0rd.ae2wtlib.sync.packets.PacketWutTerminalSelect;

/**
 * @author p455w0rd
 *
 */
public class GuiMenuButton extends GuiButton {

	protected boolean selected;
	private final GuiWUTTermSelection gui;
	//private static final Color colorSelected = Color.GREEN;
	//private static final Color colorDeselected = Color.LIGHT_GRAY;

	public GuiMenuButton(final int id, final GuiWUTTermSelection gui, final String buttonText) {
		super(id, 0, 0, 0, 0, buttonText);
		this.gui = gui;
	}

	@Override
	public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
		if (!visible) {
			return;
		}
		hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		//drawRect(x, y, x + width, y + height, (selected ? colorSelected : colorDeselected).getRGB());
		mouseDragged(mc, mouseX, mouseY);
		//final int textColor = !enabled ? 10526880 : hovered ? 16777120 : -1;
		//mc.fontRenderer.drawString(displayString, x + width / 2 - mc.fontRenderer.getStringWidth(displayString) / 2, y + (height - 8) / 2, textColor);
	}

	@Override
	public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
		final boolean pressed = super.mousePressed(mc, mouseX, mouseY);
		if (pressed) {
			click();
		}
		return pressed;
	}

	public void click() {
		final int thisIndex = ItemWUT.getStoredTerminalStacks(getGui().getTerminal()).get(id).getRight();
		final int selectedIndex = ItemWUT.getSelectedTerminalStack(getGui().getTerminal()).getRight();
		if (thisIndex != selectedIndex) {
			toggleSelected();
			final EntityPlayer player = Minecraft.getMinecraft().player;
			ItemWUT.setSelectedTerminal(getGui().getTerminal(), thisIndex);
			LibNetworking.instance().sendToServer(new PacketWutTerminalSelect(thisIndex));
			getGui().mc.player.closeScreen();
			((ICustomWirelessTerminalItem) player.getHeldItemMainhand().getItem()).openGui(player, false, player.inventory.currentItem);
			//getGui().mc.player.sendMessage(new TextComponentString(TextFormatting.ITALIC + "" + ItemWUT.getSelectedTerminalStack(getGui().getTerminal()).getLeft().getDisplayName() + " Selected"));
			//
		}
	}

	private GuiWUTTermSelection getGui() {
		return gui;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(final boolean isSelected) {
		selected = isSelected;
	}

	public void toggleSelected() {
		selected = !selected;
	}

}
