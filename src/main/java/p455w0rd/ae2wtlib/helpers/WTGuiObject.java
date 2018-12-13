package p455w0rd.ae2wtlib.helpers;

import java.util.List;

import com.google.common.collect.Lists;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.guiobjects.IGuiItemObject;
import appeng.api.implementations.tiles.IWirelessAccessPoint;
import appeng.api.networking.*;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.*;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.*;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.tile.networking.TileWireless;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.api.networking.security.WTIActionHost;

public class WTGuiObject<O extends IAEStack<O>, C extends IStorageChannel<O>> implements ITerminalHost, IMEMonitor<O>, IEnergySource, IGuiItemObject, IInventorySlotAware, WTIActionHost {

	private final ItemStack effectiveItem;
	private final IWirelessTermHandler wth;
	private final String encryptionKey;
	private final EntityPlayer myPlayer;
	private IGrid targetGrid;
	private IStorageGrid sg;
	private IMEMonitor<O> itemStorage;
	private IWirelessAccessPoint myWap;
	private double sqRange = Double.MAX_VALUE;
	private double myRange = Double.MAX_VALUE;
	private final int inventorySlot;

	@SuppressWarnings("unchecked")
	public WTGuiObject(final IWirelessTermHandler wh, final ItemStack is, final EntityPlayer ep, final World w, final int x, final int y, final int z) {
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
						if (wh instanceof ICustomWirelessTermHandler) {
							IStorageChannel<O> channel = (IStorageChannel<O>) ((ICustomWirelessTermHandler) wh).getStorageChannel();
							itemStorage = sg.getInventory(channel);
						}
					}
				}
			}
		}
	}

	public List<IWirelessAccessPoint> getWAPs() {
		List<IWirelessAccessPoint> wapList = Lists.newArrayList();
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

	public IGrid getTargetGrid() {
		return targetGrid;
	}

	public double getRange() {
		return myRange;
	}

	@Override
	public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel) {
		if (sg != null && channel != null) {
			return sg.getInventory(channel);
		}
		return null;
	}

	@Override
	public void addListener(final IMEMonitorHandlerReceiver<O> l, final Object verificationToken) {
		if (itemStorage != null) {
			itemStorage.addListener(l, verificationToken);
		}
	}

	@Override
	public void removeListener(final IMEMonitorHandlerReceiver<O> l) {
		if (itemStorage != null) {
			itemStorage.removeListener(l);
		}
	}

	/**
	 * Don't use - will be removed by AE2 soon
	 */
	@Override
	@Deprecated
	public IItemList<O> getAvailableItems(final IItemList<O> out) {
		if (itemStorage != null) {
			return itemStorage.getAvailableItems(out);
		}
		return out;
	}

	@Override
	public IItemList<O> getStorageList() {
		if (itemStorage != null) {
			return itemStorage.getStorageList();
		}
		return null;
	}

	@Override
	public AccessRestriction getAccess() {
		if (itemStorage != null) {
			return itemStorage.getAccess();
		}
		return AccessRestriction.NO_ACCESS;
	}

	@Override
	public boolean isPrioritized(final O input) {
		if (itemStorage != null) {
			return itemStorage.isPrioritized(input);
		}
		return false;
	}

	@Override
	public boolean canAccept(final O input) {
		if (itemStorage != null) {
			return itemStorage.canAccept(input);
		}
		return false;
	}

	@Override
	public int getPriority() {
		if (itemStorage != null) {
			return itemStorage.getPriority();
		}
		return 0;
	}

	@Override
	public int getSlot() {
		if (itemStorage != null) {
			return itemStorage.getSlot();
		}
		return 0;
	}

	@Override
	public boolean validForPass(final int i) {
		return itemStorage.validForPass(i);
	}

	@Override
	public O injectItems(final O input, final Actionable type, final IActionSource src) {
		if (itemStorage != null) {
			return itemStorage.injectItems(input, type, src);
		}
		return input;
	}

	@Override
	public O extractItems(final O request, final Actionable mode, final IActionSource src) {
		if (itemStorage != null) {
			return itemStorage.extractItems(request, mode, src);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IStorageChannel<O> getChannel() {
		if (itemStorage != null) {
			return itemStorage.getChannel();
		}
		if (wth instanceof ICustomWirelessTermHandler) {
			return (IStorageChannel<O>) itemStorage;
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

	public IGridNode getGridNode(final AEPartLocation dir) {
		return this.getActionableNode();
	}

	public AECableType getCableConnectionType(final AEPartLocation dir) {
		return AECableType.NONE;
	}

	@Override
	public IGridNode getActionableNode() {
		boolean ignoreRange = false;
		if (!effectiveItem.isEmpty() && WTApi.instance().isAnyWT(effectiveItem)) {
			if (effectiveItem.getItem() instanceof ICustomWirelessTerminalItem) {
				ICustomWirelessTerminalItem item = (ICustomWirelessTerminalItem) effectiveItem.getItem();
				ignoreRange = item.checkForBooster(effectiveItem);
			}
		}
		return getActionableNode(ignoreRange);
	}

	@Override
	public IGridNode getActionableNode(boolean ignoreRange) {
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
				IGrid grid = getTargetGrid();
				if (grid != null) {
					IGridNode node = grid.getPivot();
					if (node != null) {
						return node;
					}
				}
			}
		}
		return null;
	}

	public boolean rangeCheck() {
		return rangeCheck(false);
	}

	public boolean rangeCheck(boolean ignoreRange) {
		sqRange = myRange = Double.MAX_VALUE;

		if (targetGrid != null && itemStorage != null) {
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

	public boolean testWap(final IWirelessAccessPoint wap) {
		return testWap(wap, false);
	}

	public boolean testWap(final IWirelessAccessPoint wap, boolean ignoreRange) {
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
