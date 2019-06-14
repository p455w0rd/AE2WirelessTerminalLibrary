package p455w0rd.ae2wtlib.helpers;

import java.util.List;

import com.google.common.collect.Lists;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.tiles.IWirelessAccessPoint;
import appeng.api.networking.*;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.*;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.*;
import appeng.tile.networking.TileWireless;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import p455w0rd.ae2wtlib.api.*;

public class WTGuiObjectImpl<O extends IAEStack<O>, C extends IStorageChannel<O>> extends WTGuiObject<O> {

	private final ItemStack effectiveItem;
	private final IWirelessTermHandler wth;
	private final String encryptionKey;
	private final EntityPlayer myPlayer;
	private IGrid targetGrid;
	private IStorageGrid sg;
	private IMEMonitor<O> objectStorage;
	private IWirelessAccessPoint myWap;
	private double sqRange = Double.MAX_VALUE;
	private double myRange = Double.MAX_VALUE;
	private final int inventorySlot;

	@SuppressWarnings("unchecked")
	public WTGuiObjectImpl(final IWirelessTermHandler wh, final ItemStack is, final EntityPlayer ep, final World w, final int x, final int y, final int z) {
		encryptionKey = wh.getEncryptionKey(is);
		effectiveItem = is;
		myPlayer = ep;
		wth = wh;
		inventorySlot = x;
		ILocatable obj = null;

		try {
			final long encKey = Long.parseLong(encryptionKey);
			obj = AEApi.instance().registries().locatable().getLocatableBy(encKey);
		}
		catch (final NumberFormatException err) {
		}

		if (obj != null && obj instanceof IGridHost) {
			final IGridNode n = ((IGridHost) obj).getGridNode(AEPartLocation.INTERNAL);
			if (n != null) {
				targetGrid = n.getGrid();
				if (targetGrid != null) {
					sg = targetGrid.getCache(IStorageGrid.class);
					if (sg != null) {
						if (wh instanceof ICustomWirelessTerminalItem) {
							final IStorageChannel<O> channel = (IStorageChannel<O>) ((ICustomWirelessTerminalItem) wh).getStorageChannel(is);
							objectStorage = sg.getInventory(channel);
						}
					}
				}
			}
		}
	}

	@Override
	public List<IWirelessAccessPoint> getWAPs() {
		final List<IWirelessAccessPoint> wapList = Lists.newArrayList();
		if (targetGrid != null) {
			final IMachineSet tw = targetGrid.getMachines(TileWireless.class);
			for (final IGridNode n : tw) {
				if (n.getMachine() instanceof IWirelessAccessPoint) {
					final IWirelessAccessPoint wap = (IWirelessAccessPoint) n.getMachine();
					wapList.add(wap);
				}
			}
		}
		return wapList;
	}

	@Override
	public IWirelessAccessPoint getWAP() {
		if (myWap == null) {
			if (targetGrid != null) {
				final IMachineSet tw = targetGrid.getMachines(TileWireless.class);
				for (final IGridNode n : tw) {
					if (n.getMachine() instanceof IWirelessAccessPoint) {
						final IWirelessAccessPoint wap = (IWirelessAccessPoint) n.getMachine();
						myWap = wap;
					}
				}
			}
		}
		return myWap;
	}

	@Override
	public IGrid getTargetGrid() {
		return targetGrid;
	}

	@Override
	public double getRange() {
		return myRange;
	}

	@Override
	public <T extends IAEStack<T>> IMEMonitor<T> getInventory(final IStorageChannel<T> channel) {
		if (sg != null && channel != null) {
			return sg.getInventory(channel);
		}
		return null;
	}

	@Override
	public void addListener(final IMEMonitorHandlerReceiver<O> l, final Object verificationToken) {
		if (objectStorage != null) {
			objectStorage.addListener(l, verificationToken);
		}
	}

	@Override
	public void removeListener(final IMEMonitorHandlerReceiver<O> l) {
		if (objectStorage != null) {
			objectStorage.removeListener(l);
		}
	}

	/**
	 * Don't use - will be removed by AE2 soon
	 */
	@Override
	@Deprecated
	public IItemList<O> getAvailableItems(final IItemList<O> out) {
		if (objectStorage != null) {
			return objectStorage.getAvailableItems(out);
		}
		return out;
	}

	@Override
	public IItemList<O> getStorageList() {
		if (objectStorage != null) {
			return objectStorage.getStorageList();
		}
		return null;
	}

	@Override
	public AccessRestriction getAccess() {
		if (objectStorage != null) {
			return objectStorage.getAccess();
		}
		return AccessRestriction.NO_ACCESS;
	}

	@Override
	public boolean isPrioritized(final O input) {
		if (objectStorage != null) {
			return objectStorage.isPrioritized(input);
		}
		return false;
	}

	@Override
	public boolean canAccept(final O input) {
		if (objectStorage != null) {
			return objectStorage.canAccept(input);
		}
		return false;
	}

	@Override
	public int getPriority() {
		if (objectStorage != null) {
			return objectStorage.getPriority();
		}
		return 0;
	}

	@Override
	public int getSlot() {
		if (objectStorage != null) {
			return objectStorage.getSlot();
		}
		return 0;
	}

	@Override
	public boolean validForPass(final int i) {
		return objectStorage.validForPass(i);
	}

	@Override
	public O injectItems(final O input, final Actionable type, final IActionSource src) {
		if (objectStorage != null) {
			return objectStorage.injectItems(input, type, src);
		}
		return input;
	}

	@Override
	public O extractItems(final O request, final Actionable mode, final IActionSource src) {
		if (objectStorage != null) {
			return objectStorage.extractItems(request, mode, src);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IStorageChannel<O> getChannel() {
		if (objectStorage != null) {
			return objectStorage.getChannel();
		}
		if (wth instanceof ICustomWirelessTerminalItem) {
			return (IStorageChannel<O>) objectStorage;
		}
		return null;
	}

	@Override
	public double extractAEPower(final double amt, final Actionable mode, final PowerMultiplier usePowerMultiplier) {
		if (wth != null && effectiveItem != null) {
			if (mode == Actionable.SIMULATE) {
				return wth.hasPower(myPlayer, amt, effectiveItem) ? amt : 0;
			}
			return wth.usePower(myPlayer, amt, effectiveItem) ? amt : 0;
		}
		return 0.0;
	}

	@Override
	public ItemStack getItemStack() {
		return effectiveItem;
	}

	@Override
	public IConfigManager getConfigManager() {
		return wth.getConfigManager(effectiveItem);
	}

	@Override
	public IGridNode getGridNode(final AEPartLocation dir) {
		return this.getActionableNode();
	}

	@Override
	public AECableType getCableConnectionType(final AEPartLocation dir) {
		return AECableType.NONE;
	}

	@Override
	public IGridNode getActionableNode() {
		boolean ignoreRange = false;
		if (!effectiveItem.isEmpty() && WTApi.instance().isAnyWT(effectiveItem)) {
			if (effectiveItem.getItem() instanceof ICustomWirelessTerminalItem) {
				final ICustomWirelessTerminalItem item = (ICustomWirelessTerminalItem) effectiveItem.getItem();
				ignoreRange = item.hasInfiniteRange(effectiveItem);
			}
		}
		return getActionableNode(ignoreRange);
	}

	@Override
	public IGridNode getActionableNode(final boolean ignoreRange) {
		this.rangeCheck(ignoreRange);
		if (myWap != null) {
			if (myWap.getActionableNode() != null) {
				return myWap.getActionableNode();
			}
			else if (getTargetGrid() != null) {
				return getTargetGrid().getPivot();
			}

		}
		else {
			if (ignoreRange) {
				final IGrid grid = getTargetGrid();
				if (grid != null) {
					final IGridNode node = grid.getPivot();
					if (node != null) {
						return node;
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean rangeCheck() {
		return rangeCheck(false);
	}

	@Override
	public boolean rangeCheck(final boolean ignoreRange) {
		sqRange = myRange = Double.MAX_VALUE;

		if (targetGrid != null && objectStorage != null) {
			if (myWap != null) {
				if (myWap.getGrid() == targetGrid) {
					if (this.testWap(myWap)) {
						return true;
					}
				}
				return false;
			}

			final IMachineSet tw = targetGrid.getMachines(TileWireless.class);

			myWap = null;

			for (final IGridNode n : tw) {
				final IWirelessAccessPoint wap = (IWirelessAccessPoint) n.getMachine();
				if (this.testWap(wap, ignoreRange)) {
					myWap = wap;
				}
			}

			return myWap != null;
		}
		return false;
	}

	@Override
	public boolean testWap(final IWirelessAccessPoint wap) {
		return testWap(wap, false);
	}

	@Override
	public boolean testWap(final IWirelessAccessPoint wap, final boolean ignoreRange) {
		double rangeLimit = wap.getRange();
		rangeLimit *= rangeLimit;

		final DimensionalCoord dc = wap.getLocation();

		if (dc.getWorld() == myPlayer.getEntityWorld()) {
			final double offX = dc.x - myPlayer.posX;
			final double offY = dc.y - myPlayer.posY;
			final double offZ = dc.z - myPlayer.posZ;

			final double r = offX * offX + offY * offY + offZ * offZ;
			if (r < rangeLimit && sqRange > r && !ignoreRange) {
				if (wap.isActive()) {
					sqRange = r;
					myRange = Math.sqrt(r);
					return true;
				}
			}
			else {
				if (wap.isActive() && ignoreRange) {
					sqRange = r;
					myRange = Math.sqrt(r);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int getInventorySlot() {
		return inventorySlot;
	}

}
