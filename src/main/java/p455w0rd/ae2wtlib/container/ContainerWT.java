package p455w0rd.ae2wtlib.container;

import java.util.*;

import javax.annotation.Nonnull;

import appeng.api.config.*;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.container.AEBaseContainer;
import appeng.container.guisync.GuiSync;
import appeng.container.guisync.SyncData;
import appeng.container.slot.AppEngSlot;
import appeng.container.slot.SlotDisabled;
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
import p455w0rd.ae2wtlib.api.networking.security.WTIActionHost;
import p455w0rd.ae2wtlib.api.networking.security.WTPlayerSource;
import p455w0rd.ae2wtlib.container.slot.*;
import p455w0rd.ae2wtlib.helpers.WTGuiObject;
import p455w0rd.ae2wtlib.init.LibConfig;
import p455w0rd.ae2wtlib.init.LibIntegration.Mods;
import p455w0rd.ae2wtlib.init.LibNetworking;
import p455w0rd.ae2wtlib.integration.Baubles;
import p455w0rd.ae2wtlib.inventory.WTInventoryBooster;
import p455w0rd.ae2wtlib.sync.packets.PacketSetInRange;

public class ContainerWT extends AEBaseContainer implements IWTContainer, IConfigurableObject, IConfigManagerHost, IAEAppEngInventory {

	private WTGuiObject<?, ?> obj;
	protected WTInventoryBooster boosterInventory = new WTInventoryBooster(this);
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

	public ContainerWT(final InventoryPlayer ip, final Object anchor) {
		this(ip, anchor, -1, false);
	}

	public ContainerWT(final InventoryPlayer ip, final Object anchor, int wtSlot, boolean isBauble) {
		super(ip, null, null);
		player = ip.player;
		this.wtSlot = wtSlot;
		this.isBauble = isBauble;
		containerstack = isBauble ? Baubles.getWTBySlot(player, wtSlot, ICustomWirelessTerminalItem.class) : WTApi.instance().getWTBySlot(player, wtSlot);
		obj = anchor instanceof WTGuiObject ? (WTGuiObject<?, ?>) anchor : null;
		if (obj == null) {
			setValidContainer(false);
		}
		else {
			ReflectionHelper.setPrivateValue(AEBaseContainer.class, this, new WTPlayerSource(ip.player, getActionHost(obj)), "mySrc");
		}
	}

	protected void setTerminalHost(ITerminalHost host) {
		this.host = host;
	}

	protected ITerminalHost getTerminalHost() {
		return host;
	}

	protected IConfigManager setServerConfigManager(IConfigManager mgr) {
		serverCM = mgr;
		return serverCM;
	}

	protected IConfigManager getServerConfigManager() {
		return serverCM;
	}

	protected IConfigManager setClientConfigManager(IConfigManager mgr) {
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

	public void setWirelessTerminal(@Nonnull ItemStack wirelessTerminal) {
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
		NBTTagCompound newNBT = getWirelessTerminal().getTagCompound();
		if (boosterInventory != null) {
			newNBT.setTag("BoosterSlot", boosterInventory.serializeNBT());
		}
		getWirelessTerminal().setTagCompound(newNBT);
		if (Mods.BAUBLES.isLoaded() && isWTBauble()) {
			Baubles.sync(getPlayerInv().player, getWirelessTerminal(), getWTSlot());
		}
	}

	protected void initConfig(IConfigManager cm) {
		cm.registerSetting(Settings.SORT_BY, SortOrder.NAME);
		cm.registerSetting(Settings.VIEW_MODE, ViewItems.ALL);
		cm.registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING);
	}

	protected void setGuiObject(WTGuiObject<?, ?> guiObject) {
		obj = guiObject;
	}

	public WTGuiObject<?, ?> getGuiObject() {
		return obj;
	}

	protected static WTIActionHost getActionHost(Object object) {
		if (object instanceof WTIActionHost) {
			return (WTIActionHost) object;
		}
		return null;
	}

	@Override
	public void setTargetStack(final IAEItemStack stack) {
		//TODO be sure to override in extending container
		/*
		if (Platform.isClient()) {

			if (stack == null && getTargetStack() == null) {
				return;
			}
			if (stack != null && stack.isSameType(getTargetStack())) {
				return;
			}
			ModNetworking.instance().sendToServer(new PacketTargetItemStack((AEItemStack) stack));
		}
		ReflectionHelper.setPrivateValue(AEBaseContainer.class, this, stack == null ? null : stack.copy(), "clientRequestedTargetItem");
		*/
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
		IItemHandler ih = new PlayerInvWrapper(inventoryPlayer);
		HashSet<Integer> locked = ReflectionHelper.getPrivateValue(AEBaseContainer.class, this, "locked");
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
			BlockPos playerPos = getPlayerInv().player.getPosition();
			setGuiObject(new WTGuiObject<IAEItemStack, IItemStorageChannel>((IWirelessTermHandler) getWirelessTerminal().getItem(), getWirelessTerminal(), getPlayerInv().player, getPlayerInv().player.getEntityWorld(), playerPos.getX(), playerPos.getY(), playerPos.getZ()));
		}
		if (getGuiObject().getWAP() != null) {
			double wapRange = getGuiObject().getWAP().getRange();
			BlockPos wapPos = getGuiObject().getWAP().getLocation().getPos();
			BlockPos playerPos = getPlayerInv().player.getPosition();
			double distanceToWap = Math.sqrt(playerPos.distanceSq(wapPos));
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

	protected boolean isInHotbar(@Nonnull AppEngSlot slot) {
		return slot instanceof SlotPlayerHotBar && InventoryPlayer.isHotbar(slot.getSlotIndex());
	}

	protected boolean isInInventory(@Nonnull AppEngSlot slot) {
		if (slot instanceof SlotPlayerInv) {
			return slot.slotNumber >= 0 && slot.slotNumber <= 27;
		}
		return false;
	}

	public int getBoosterIndex() {
		if (getBoosterSlot() != null) {
			for (int i = 0; i < inventorySlots.size(); i++) {
				if ((inventorySlots.get(i) instanceof SlotBooster) || (inventorySlots.get(i) instanceof SlotBoosterEnergy)) {
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
				if (!LibConfig.USE_OLD_INFINTY_MECHANIC) {
					//if (!WCTUtils.isInRange(getWirelessTerminal())) {
					WTApi.instance().setInRange(getWirelessTerminal(), true);
					LibNetworking.instance().sendTo(new PacketSetInRange(true), (EntityPlayerMP) getPlayerInv().player);
					//}
				}
			}
			else {
				obj.extractAEPower((int) (Math.min(500.0, AEConfig.instance().wireless_getDrainRate(obj.getRange()))), Actionable.MODULATE, PowerMultiplier.CONFIG);
				if (!LibConfig.USE_OLD_INFINTY_MECHANIC) {
					//if (WCTUtils.isInRange(getWirelessTerminal())) {
					WTApi.instance().setInRange(getWirelessTerminal(), false);
					LibNetworking.instance().sendTo(new PacketSetInRange(false), (EntityPlayerMP) getPlayerInv().player);
					//}
					WTApi.instance().drainInfinityEnergy(getWirelessTerminal(), getPlayerInv().player, isBauble, wtSlot);
				}
			}
			ticks = 0;
		}

		sendCustomName();

		for (final IContainerListener listener : listeners) {
			HashMap<Integer, SyncData> syncData = ReflectionHelper.getPrivateValue(AEBaseContainer.class, this, "syncData");
			for (final SyncData sd : syncData.values()) {
				sd.tick(listener);
			}
		}
		for (int i = 0; i < inventorySlots.size(); ++i) {
			ItemStack itemstack = inventorySlots.get(i).getStack();
			ItemStack itemstack1 = inventoryItemStacks.get(i);

			if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
				boolean clientStackChanged = !ItemStack.areItemStacksEqualUsingNBTShareTag(itemstack1, itemstack);
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
