package p455w0rd.ae2wtlib.api.client.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import p455w0rd.ae2wtlib.api.client.gui.GuiWT;

/**
 * @author p455w0rd
 *
 */
public class GuiButtonPanel {

	final List<GuiButton> buttonList = new ArrayList<>();
	int x, y;

	public GuiButtonPanel(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void init(GuiWT gui) {
		for (GuiButton button : buttonList) {
			gui.getButtonList().add(button);
		}
	}

	public GuiButtonPanel setX(int x) {
		this.x = x;
		return this;
	}

	public GuiButtonPanel setY(int y) {
		this.y = y;
		return this;
	}

	public void addButton(GuiButton button) {
		buttonList.add(button);
	}

	public int getButtonPanelYOffset() {
		return y + (buttonList.size() * 20);
	}

	public int getButtonPanelXOffset() {
		return x;
	}

	public boolean isEmpty() {
		return buttonList.isEmpty();
	}

}
