package p455w0rd.ae2wtlib.api.client;

import appeng.client.gui.widgets.IScrollSource;
import p455w0rd.ae2wtlib.api.base.GuiWT;

/**
 * @author p455w0rd
 *
 */
public interface IWTGuiScrollbar extends IScrollSource {

	void draw(GuiWT gui);

	void click(final GuiWT gui, final int x, final int y);

	void wheel(int delta);

	IWTGuiScrollbar setTop(int value);

	IWTGuiScrollbar setLeft(int value);

	IWTGuiScrollbar setHeight(int value);

	void setRange(int min, int max, int pageSize);

}
