package p455w0rd.ae2wtlib.api.client;

import appeng.api.storage.data.IAEStack;
import net.minecraft.client.gui.FontRenderer;

/**
 * @author AlgorithmX2
 * @author thatsIch
 * @author yueh
 * @author p455w0rd
 *
 * Consolidated classes - p455w0rd
 *
 * @version rv6
 * @since rv6
 */
public abstract class StackSizeRenderer<T extends IAEStack<T>> {

	public abstract void renderStackSize(FontRenderer fontRenderer, T aeStack, int xPos, int yPos);

	public ISlimReadableNumberConverter getSlimConverter() {
		return ReadableNumberConverter.INSTANCE;
	}

	public IWideReadableNumberConverter getWideConverter() {
		return ReadableNumberConverter.INSTANCE;
	}

}
