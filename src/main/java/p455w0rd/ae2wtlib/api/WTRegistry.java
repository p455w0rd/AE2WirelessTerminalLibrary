package p455w0rd.ae2wtlib.api;

import java.util.List;

/**
 * @author p455w0rd
 *
 */
public abstract class WTRegistry {

	public abstract List<? extends ICustomWirelessTerminalItem> getRegisteredTerminals();

	public abstract void registerWirelessTerminal(ICustomWirelessTerminalItem wirelessTerminal);

}
