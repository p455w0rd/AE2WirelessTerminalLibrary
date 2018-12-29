package p455w0rd.ae2wtlib.api;

import java.util.List;

import appeng.api.implementations.guiobjects.IGuiItemObject;
import appeng.api.implementations.tiles.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.container.interfaces.IInventorySlotAware;
import p455w0rd.ae2wtlib.api.networking.security.WTIActionHost;

/**
 * @author p455w0rd
 *
 */
public abstract class WTGuiObject<X extends IAEStack<X>> implements ITerminalHost, IMEMonitor<X>, IEnergySource, IGuiItemObject, IInventorySlotAware, WTIActionHost {

	public abstract List<IWirelessAccessPoint> getWAPs();

	public abstract IWirelessAccessPoint getWAP();

	public abstract IGrid getTargetGrid();

	public abstract double getRange();

	public abstract IGridNode getGridNode(final AEPartLocation dir);

	public abstract AECableType getCableConnectionType(final AEPartLocation dir);

	public abstract boolean rangeCheck();

	public abstract boolean rangeCheck(boolean ignoreRange);

	public abstract boolean testWap(final IWirelessAccessPoint wap);

	public abstract boolean testWap(final IWirelessAccessPoint wap, boolean ignoreRange);

}
