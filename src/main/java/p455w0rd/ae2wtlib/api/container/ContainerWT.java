package p455w0rd.ae2wtlib.api.container;

import java.util.*;

import javax.annotation.Nonnull;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.client.me.InternalSlotME;
import appeng.container.AEBaseContainer;
import appeng.container.guisync.GuiSync;
import appeng.container.guisync.SyncData;
import appeng.container.slot.*;
import appeng.core.AEConfig;
import appeng.helpers.InventoryAction;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.api.container.slot.SlotBooster;
import p455w0rd.ae2wtlib.api.container.slot.SlotBoosterEnergy;
import p455w0rd.ae2wtlib.api.container.slot.SlotPlayerHotBar;
import p455w0rd.ae2wtlib.api.container.slot.SlotPlayerInv;
import p455w0rd.ae2wtlib.api.inventory.WTInventoryBooster;
import p455w0rd.ae2wtlib.api.networking.security.WTIActionHost;
import p455w0rd.ae2wtlib.api.networking.security.WTPlayerSource;
import p455w0rdslib.LibGlobals.Mods;

public class ContainerWT extends AEBaseContainer implements IWTContainer, IConfigurableObject, IConfigManagerHost, IAEAppEngInventory {

	private WTGuiObject<?> obj;
	private final WTInventoryBooster boosterInventory = new WTInventoryBooster(this);
	protected AppEngSlot boosterSlot;
	private ItemStack containerstack;
	private final EntityPlayer player;
	private int wtSlot = -1;
	private boolean isBauble = false;
	private ITerminalHost host;
	private IConfigManager clientCM;
	private IConfigManager serverCM;
	private int ticks = 0;
	@GuiSync(200)
	public static boolean hasPower = false;
	private IConfigManagerHost gui;
	boolean boosterSlotEnabled = false;
	int boosterSlotPosX = 0;
	int boosterSlotPosY = 0;

	public ContainerWT(final InventoryPlayer ip, final Object anchor) {
		this(ip, anchor, -1, false);
	}

	public ContainerWT(final InventoryPlayer ip, final Object anchor, final int wtSlot, final boolean isBauble) {
		this(ip, anchor, wtSlot, isBauble, false, 0, 0);
	}

	public ContainerWT(final InventoryPlayer ip, final Object anchor, final int wtSlot, final boolean isBauble, final boolean enableBoosterSlot, final int boosterSlotPosX, final int boosterSlotPosY) {
		super(ip, null, null);
		player = ip.player;
		this.wtSlot = wtSlot;
		this.isBauble = isBauble;
		containerstack = isBauble ? WTApi.instance().getBaublesUtility().getWTBySlot(player, wtSlot, ICustomWirelessTerminalItem.class) : WTApi.instance().getWTBySlot(player, wtSlot);
		obj = anchor instanceof WTGuiObject ? (WTGuiObject<?>) anchor : WTApi.instance().getGUIObject(WTApi.instance().getWTBySlot(player, isBauble, wtSlot, ICustomWirelessTerminalItem.class), player);
		if (obj == null) {
			setValidContainer(false);
		}
		else {
			ReflectionHelper.setPrivateValue(AEBaseContainer.class, this, new WTPlayerSource(ip.player, getActionHost(obj)), "mySrc");
		}
		boosterSlotEnabled = enableBoosterSlot;
		if (boosterSlotEnabled) {
			this.boosterSlotPosX = boosterSlotPosX;
			this.boosterSlotPosY = boosterSlotPosY;
			if (WTApi.instance().getConfig().isInfinityBoosterCardEnabled() && !WTApi.instance().isWTCreative(getWirelessTerminal())) {
				if (WTApi.instance().getConfig().isOldInfinityMechanicEnabled()) {
					addSlotToContainer(boosterSlot = WTApi.instance().createOldBoosterSlot(getBoosterInventory(), boosterSlotPosX, boosterSlotPosY));
					boosterSlot.setContainer(this);
				}
				else {
					addSlotToContainer(boosterSlot = WTApi.instance().createInfinityBoosterSlot(boosterSlotPosX, boosterSlotPosY));//new SlotBoosterEnergy(134, -20));
					boosterSlot.setContainer(this);
				}
			}
			else {
				addSlotToContainer(boosterSlot = WTApi.instance().createNullSlot());
				boosterSlot.setContainer(this);
			}
		}
		((ICustomWirelessTerminalItem) getWirelessTerminal().getItem()).hasInfiniteRange(getWirelessTerminal());
	}

	protected WTInventoryBooster getBoosterInventory() {
		return boosterInventory;
	}

	protected void setTerminalHost(final ITerminalHost host) {
		this.host = host;
	}

	protected ITerminalHost getTerminalHost() {
		return host;
	}

	protected IConfigManager setServerConfigManager(final IConfigManager mgr) {
		serverCM = mgr;
		return serverCM;
	}

	protected IConfigManager getServerConfigManager() {
		return serverCM;
	}

	protected IConfigManager setClientConfigManager(final IConfigManager mgr) {
		clientCM = mgr;
		return clientCM;
	}

	protected IConfigManager getClientConfigManager() {
		return clientCM;
	}

	@Override
	public IConfigManager getConfigManager() {
		if (Platform.isServer()) {
			return serverCM;
		}
		return clientCM;
	}

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer p, final int idx) {
		AppEngSlot appEngSlot = null;
		ItemStack tis = ItemStack.EMPTY;
		boolean isAppengSlot = false;
		if (inventorySlots.get(idx) instanceof AppEngSlot) {
			isAppengSlot = true;
			appEngSlot = (AppEngSlot) inventorySlots.get(idx);
			tis = appEngSlot.getStack();
		}
		if (tis.isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (isAppengSlot && appEngSlot != null && appEngSlot.getHasStack()) {
			if (Platform.isClient()) {
				return ItemStack.EMPTY;
			}
			boolean hasMETiles = false;
			for (final Object is : inventorySlots) {
				if (is instanceof InternalSlotME) {
					hasMETiles = true;
					break;
				}
			}
			if (hasMETiles) {
				return ItemStack.EMPTY;
			}
			if (appEngSlot instanceof SlotDisabled || appEngSlot instanceof SlotInaccessible) {
				return ItemStack.EMPTY;
			}
			if (appEngSlot != null && appEngSlot.getHasStack()) {
				if (isInInventory(appEngSlot) || isInHotbar(appEngSlot)) {
					if (tis.getItem() == WTApi.instance().getBoosterCard()) {
						if (mergeItemStack(tis.copy(), getBoosterIndex(), getBoosterIndex() + 1, false)) {
							if (tis.getCount() > 1 && WTApi.instance().getConfig().isOldInfinityMechanicEnabled()) {
								tis.shrink(1);
							}
							else {
								appEngSlot.clearStack();
							}
							if (WTApi.instance().getConfig().isInfinityBoosterCardEnabled() && !WTApi.instance().getConfig().isOldInfinityMechanicEnabled()) {
								final int currentInfinityEnergy = WTApi.instance().getInfinityEnergy(getWirelessTerminal());
								WTApi.instance().getNetHandler().sendTo(WTApi.instance().getNetHandler().createInfinityEnergySyncPacket(currentInfinityEnergy, getPlayer().getUniqueID(), isWTBauble(), getWTSlot()), (EntityPlayerMP) getPlayer());
							}
							//appEngSlot.putStack(!tis.isEmpty() ? tis.copy() : ItemStack.EMPTY);
							appEngSlot.onSlotChanged();
							return ItemStack.EMPTY;
						}
					}
				}
			}
		}
		return tis;
	}

	@Override
	protected boolean mergeItemStack(final ItemStack stack, final int start, final int end, final boolean backwards) {
		boolean flag1 = false;
		int k = backwards ? end - 1 : start;
		Slot slot;
		ItemStack itemstack1;

		if (stack.isStackable()) {
			while (stack.getCount() > 0 && (!backwards && k < end || backwards && k >= start)) {
				slot = inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (!slot.isItemValid(stack)) {
					k += backwards ? -1 : 1;
					continue;
				}

				if (!itemstack1.isEmpty() && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, itemstack1)) {
					final int l = itemstack1.getCount() + stack.getCount();

					if (l <= stack.getMaxStackSize() && l <= slot.getSlotStackLimit()) {
						stack.setCount(0);
						itemstack1.setCount(l);
						flag1 = true;
					}
					else if (itemstack1.getCount() < stack.getMaxStackSize() && l < slot.getSlotStackLimit()) {
						stack.shrink(stack.getMaxStackSize() - itemstack1.getCount());
						itemstack1.setCount(stack.getMaxStackSize());
						flag1 = true;
					}
				}

				k += backwards ? -1 : 1;
			}
		}
		if (stack.getCount() > 0) {
			k = backwards ? end - 1 : start;
			while (!backwards && k < end || backwards && k >= start) {
				slot = inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (!slot.isItemValid(stack)) {
					k += backwards ? -1 : 1;
					continue;
				}

				if (itemstack1.isEmpty()) {
					final int l = stack.getCount();
					if (l <= slot.getSlotStackLimit()) {
						slot.putStack(stack.copy());
						stack.setCount(0);
						flag1 = true;
						break;
					}
					else {
						putStackInSlot(k, new ItemStack(stack.getItem(), slot.getSlotStackLimit(), stack.getItemDamage()));
						stack.shrink(slot.getSlotStackLimit());
						flag1 = true;
					}
				}
				k += backwards ? -1 : 1;
			}
		}
		writeToNBT();
		detectAndSendChanges();
		return flag1;
	}

	@Override
	public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {
		if (getGui() != null) {
			getGui().updateSetting(manager, settingName, newValue);
		}
	}

	private IConfigManagerHost getGui() {
		return gui;
	}

	public void setGui(@Nonnull final IConfigManagerHost gui) {
		this.gui = gui;
	}

	public List<IContainerListener> getListeners() {
		return listeners;
	}

	public void setWirelessTerminal(@Nonnull final ItemStack wirelessTerminal) {
		containerstack = wirelessTerminal;
	}

	@Override
	public ItemStack getWirelessTerminal() {
		return containerstack;
	}

	@Override
	public boolean isWTBauble() {
		return isBauble;
	}

	@Override
	public int getWTSlot() {
		return wtSlot;
	}

	@Override
	public EntityPlayer getPlayer() {
		return player;
	}

	@Override
	public void saveChanges() {
	}

	@Override
	public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removedStack, final ItemStack newStack) {
	}

	public AppEngSlot getTrashSlot() {
		return null;
	}

	public void readNBT() {
		if (boosterInventory != null && getWirelessTerminal().hasTagCompound()) {
			boosterInventory.readFromNBT(getWirelessTerminal().getTagCompound(), "BoosterSlot");
		}
	}

	public void writeToNBT() {
		if (!getWirelessTerminal().hasTagCompound()) {
			getWirelessTerminal().setTagCompound(new NBTTagCompound());
		}
		final NBTTagCompound newNBT = getWirelessTerminal().getTagCompound();
		if (boosterInventory != null) {
			newNBT.setTag("BoosterSlot", boosterInventory.serializeNBT());
		}
		getWirelessTerminal().setTagCompound(newNBT);
		if (Mods.BAUBLES.isLoaded() && isWTBauble()) {
			WTApi.instance().getBaublesUtility().sync(getPlayerInv().player, getWirelessTerminal(), getWTSlot());
		}
	}

	protected void initConfig(final IConfigManager cm) {
	}

	protected void setGuiObject(final WTGuiObject<?> guiObject) {
		obj = guiObject;
	}

	public WTGuiObject<?> getGuiObject() {
		return obj;
	}

	public static WTGuiObject<?> getGuiObject(final ItemStack it, final EntityPlayer player) {
		if (!it.isEmpty()) {
			final IWirelessTermHandler wh = AEApi.instance().registries().wireless().getWirelessTerminalHandler(it);
			if (wh instanceof ICustomWirelessTerminalItem) {
				return WTApi.instance().getGUIObject((ICustomWirelessTerminalItem) wh, it, player);
			}
		}
		return null;
	}

	protected static WTIActionHost getActionHost(final Object object) {
		if (object instanceof WTIActionHost) {
			return (WTIActionHost) object;
		}
		return null;
	}

	@Override
	public void setTargetStack(final IAEItemStack stack) {
	}

	@Override
	protected boolean hasAccess(final SecurityPermissions perm, final boolean requirePower) {
		final IGrid grid = obj.getTargetGrid();
		if (grid != null) {
			final IEnergyGrid eg = grid.getCache(IEnergyGrid.class);
			if (!eg.isNetworkPowered()) {
				return false;
			}
		}
		final ISecurityGrid sg = grid.getCache(ISecurityGrid.class);
		if (sg.hasPermission(getInventoryPlayer().player, perm)) {
			return true;
		}
		return false;
	}

	@Override
	public Object getTarget() {
		if (obj != null) {
			return obj;
		}
		return null;
	}

	@Override
	protected void bindPlayerInventory(final InventoryPlayer inventoryPlayer, final int offsetX, final int offsetY) {
		final IItemHandler ih = new PlayerInvWrapper(inventoryPlayer);
		final HashSet<Integer> locked = ReflectionHelper.getPrivateValue(AEBaseContainer.class, this, "locked");
		// bind player inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				if (locked.contains(j + i * 9 + 9)) {
					addSlotToContainer(new SlotDisabled(ih, j + i * 9 + 9, j * 18 + offsetX, offsetY + i * 18));
				}
				else {
					addSlotToContainer(new SlotPlayerInv(ih, j + i * 9 + 9, j * 18 + offsetX, offsetY + i * 18));
				}
			}
		}

		// bind player hotbar
		for (int i = 0; i < 9; i++) {
			if (locked.contains(i)) {
				addSlotToContainer(new SlotDisabled(ih, i, i * 18 + offsetX, offsetY + 58));
			}
			else {
				addSlotToContainer(new SlotPlayerHotBar(ih, i, i * 18 + offsetX, offsetY + 58));
			}
		}
	}

	public IItemHandler getInventoryByName(final String name) {
		return null;
	}

	@Override
	public Slot addSlotToContainer(final Slot newSlot) {
		if (newSlot instanceof AppEngSlot) {
			((AppEngSlot) newSlot).setContainer(this);
		}
		return super.addSlotToContainer(newSlot);
	}

	protected boolean isInRange() {
		return obj.rangeCheck(hasInfiniteRange());
	}

	protected boolean hasInfiniteRange() {
		return WTApi.instance().hasInfiniteRange(containerstack);
	}

	public boolean isInVanillaWAPRange() {
		if (getGuiObject() == null) {
			//BlockPos playerPos = getPlayerInv().player.getPosition();
			setGuiObject(WTApi.instance().getGUIObject((ICustomWirelessTerminalItem) getWirelessTerminal().getItem(), getWirelessTerminal(), getPlayerInv().player));
			//setGuiObject(new WTGuiObjectImpl<>((IWirelessTermHandler) getWirelessTerminal().getItem(), getWirelessTerminal(), getPlayerInv().player, getPlayerInv().player.getEntityWorld(), playerPos.getX(), playerPos.getY(), playerPos.getZ()));
		}
		if (getGuiObject().getWAP() != null) {
			final double wapRange = getGuiObject().getWAP().getRange();
			final BlockPos wapPos = getGuiObject().getWAP().getLocation().getPos();
			final BlockPos playerPos = getPlayerInv().player.getPosition();
			final double distanceToWap = Math.sqrt(playerPos.distanceSq(wapPos));
			return distanceToWap <= wapRange;
		}
		return false;
	}

	protected boolean networkIsPowered() {
		final WTIActionHost host = getActionHost(getGuiObject());
		if (host != null) {
			final IGrid grid = getGuiObject().getTargetGrid();
			if (grid != null) {
				final IEnergyGrid eg = grid.getCache(IEnergyGrid.class);
				if (eg.isNetworkPowered()) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isInHotbar(@Nonnull final AppEngSlot slot) {
		return slot instanceof SlotPlayerHotBar && InventoryPlayer.isHotbar(slot.getSlotIndex());
	}

	protected boolean isInInventory(@Nonnull final AppEngSlot slot) {
		if (slot instanceof SlotPlayerInv) {
			return slot.slotNumber >= 0 && slot.slotNumber <= 27;
		}
		return false;
	}

	public int getBoosterIndex() {
		if (getBoosterSlot() != null) {
			for (int i = 0; i < inventorySlots.size(); i++) {
				if (inventorySlots.get(i) instanceof SlotBooster || inventorySlots.get(i) instanceof SlotBoosterEnergy) {
					return i;
				}
			}
		}
		return -1;
	}

	public AppEngSlot getBoosterSlot() {
		return boosterSlot != null && boosterSlot instanceof SlotBooster ? boosterSlot : boosterSlot instanceof SlotBoosterEnergy ? boosterSlot : null;
	}

	@Override
	public void detectAndSendChanges() {
		ticks++;
		if (ticks > 10) {
			if (isInRange()) {
				obj.extractAEPower(AEConfig.instance().wireless_getDrainRate(obj.getRange()), Actionable.MODULATE, PowerMultiplier.CONFIG);
				if (!WTApi.instance().getConfig().isOldInfinityMechanicEnabled()) {
					WTApi.instance().setInRange(getWirelessTerminal(), true);
					WTApi.instance().getNetHandler().sendTo(WTApi.instance().getNetHandler().createSetInRangePacket(true, false, -1), (EntityPlayerMP) getPlayer());
				}
			}
			else {
				obj.extractAEPower((int) Math.min(500.0, AEConfig.instance().wireless_getDrainRate(obj.getRange())), Actionable.MODULATE, PowerMultiplier.CONFIG);
				if (!WTApi.instance().getConfig().isOldInfinityMechanicEnabled()) {
					WTApi.instance().setInRange(getWirelessTerminal(), false);
					WTApi.instance().getNetHandler().sendTo(WTApi.instance().getNetHandler().createSetInRangePacket(false, false, -1), (EntityPlayerMP) getPlayer());
					WTApi.instance().drainInfinityEnergy(getWirelessTerminal(), getPlayerInv().player, isBauble, wtSlot);
				}
			}
			ticks = 0;
		}

		sendCustomName();

		for (final IContainerListener listener : listeners) {
			final HashMap<Integer, SyncData> syncData = ReflectionHelper.getPrivateValue(AEBaseContainer.class, this, "syncData");
			for (final SyncData sd : syncData.values()) {
				sd.tick(listener);
			}
		}
		for (int i = 0; i < inventorySlots.size(); ++i) {
			final ItemStack itemstack = inventorySlots.get(i).getStack();
			ItemStack itemstack1 = inventoryItemStacks.get(i);

			if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
				final boolean clientStackChanged = !ItemStack.areItemStacksEqualUsingNBTShareTag(itemstack1, itemstack);
				itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
				inventoryItemStacks.set(i, itemstack1);

				if (clientStackChanged) {
					for (int j = 0; j < listeners.size(); ++j) {
						listeners.get(j).sendSlotContents(this, i, itemstack1);
					}
				}
			}
		}
	}

	@Override
	public boolean canInteractWith(final EntityPlayer entityplayer) {
		if (isValidContainer()) {
			return true;
		}
		return false;
	}

	@Override
	public void doAction(final EntityPlayerMP player, final InventoryAction action, final int slot, final long id) {
	}

	@Override
	protected void updateHeld(final EntityPlayerMP p) {
	}

	protected void sendCustomName() {
	}

	@Override
	public void onUpdate(final String field, final Object oldValue, final Object newValue) {
	}

}
