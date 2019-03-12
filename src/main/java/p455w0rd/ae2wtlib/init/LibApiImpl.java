package p455w0rd.ae2wtlib.init;

import static p455w0rd.ae2wtlib.api.WTApi.Constants.NBT.*;
import static p455w0rd.ae2wtlib.init.LibConfig.INFINITY_ENERGY_DRAIN;
import static p455w0rd.ae2wtlib.init.LibConfig.INFINITY_ENERGY_PER_BOOSTER_CARD;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import appeng.api.implementations.tiles.IWirelessAccessPoint;
import appeng.container.slot.AppEngSlot;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import p455w0rd.ae2wtlib.AE2WTLib;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.api.WTApi.Integration.Mods;
import p455w0rd.ae2wtlib.api.base.ContainerWT;
import p455w0rd.ae2wtlib.api.client.IWTGuiScrollbar;
import p455w0rd.ae2wtlib.api.client.gui.widgets.GuiScrollbar;
import p455w0rd.ae2wtlib.container.slot.*;
import p455w0rd.ae2wtlib.helpers.WTGuiObjectImpl;
import p455w0rd.ae2wtlib.integration.Baubles;
import p455w0rd.ae2wtlib.inventory.WTInventoryBooster;
import p455w0rd.ae2wtlib.items.ItemInfinityBooster;
import p455w0rd.ae2wtlib.sync.packets.PacketSyncInfinityEnergy;

/**
 * @author p455w0rd
 *
 */
public class LibApiImpl extends WTApi {

	private static LibApiImpl INSTANCE = null;
	private static WTConfigImpl CONFIG = null;
	private static LibWTRegistry REGISTRY = null;
	private static WTBaublesAccess BAUBLES = null;
	private static WTGlobals CONSTANTS = null;

	public static LibApiImpl instance() {
		if (LibApiImpl.INSTANCE == null) {
			if (!LibApiImpl.hasFinishedPreInit()) {
				return null;
			}
			LibApiImpl.INSTANCE = new LibApiImpl();
		}
		return INSTANCE;
	}

	protected static boolean hasFinishedPreInit() {
		if (AE2WTLib.PROXY.getLoaderState() == LoaderState.NOINIT) {
			LibLogger.warn("API is not available until " + LibGlobals.NAME + " starts the PreInit phase.");
			return false;
		}
		return true;
	}

	@Override
	public WTConfig getConfig() {
		if (CONFIG == null) {
			if (!LibApiImpl.hasFinishedPreInit()) {
				return null;
			}
			CONFIG = new WTConfigImpl();
		}
		return CONFIG;
	}

	@Override
	public WTRegistry getRegistry() {
		if (REGISTRY == null) {
			if (!LibApiImpl.hasFinishedPreInit()) {
				return null;
			}
			REGISTRY = new LibWTRegistry();
		}
		return REGISTRY;
	}

	@Override
	public WTBaublesAccess getBaublesUtility() {
		if (BAUBLES == null) {
			if (!LibApiImpl.hasFinishedPreInit()) {
				return null;
			}
			BAUBLES = new Baubles();
		}
		return BAUBLES;
	}

	@Override
	public WTNetworkHandler getNetHandler() {
		return LibNetworking.instance();
	}

	@Override
	public WTGlobals getConstants() {
		if (CONSTANTS == null) {
			if (!LibApiImpl.hasFinishedPreInit()) {
				return null;
			}
			CONSTANTS = new LibGlobals();
		}
		return CONSTANTS;
	}

	@Override
	public ItemInfinityBooster getBoosterCard() {
		return LibItems.BOOSTER_CARD;
	}

	@Override
	public Set<Pair<Integer, ItemStack>> getWirelessTerminals(EntityPlayer player) {
		return getWirelessTerminals(player, false);
	}

	@Override
	public Set<Pair<Integer, ItemStack>> getWirelessTerminals(EntityPlayer player, boolean isBauble) {
		if (isBauble) {
			return getBaublesUtility().getAllWTBaubles(player);
		}
		Set<Pair<Integer, ItemStack>> terminalList = Sets.newHashSet();
		NonNullList<ItemStack> playerInventory = player.inventory.mainInventory;
		for (int i = 0; i < playerInventory.size(); i++) {
			ItemStack wirelessTerm = playerInventory.get(i);
			if (isAnyWT(wirelessTerm)) {
				terminalList.add(Pair.of(i, wirelessTerm));
			}
		}
		if (Mods.BAUBLES.isLoaded()) {
			Set<Pair<Integer, ItemStack>> wtBaubles = getBaublesUtility().getAllWTBaubles(player);
			if (!wtBaubles.isEmpty()) {
				terminalList.addAll(wtBaubles);
			}
		}
		return terminalList;
	}

	// Parent pair contains a boolean which tells whether or not this is a bauble slot
	// Child pair gives the slot number and ItemStack
	@Override
	public Set<Pair<Boolean, Pair<Integer, ItemStack>>> getAllWirelessTerminals(EntityPlayer player) {
		Set<Pair<Boolean, Pair<Integer, ItemStack>>> terminalList = Sets.newHashSet();
		NonNullList<ItemStack> playerInventory = player.inventory.mainInventory;
		for (int i = 0; i < playerInventory.size(); i++) {
			ItemStack wirelessTerm = playerInventory.get(i);
			if (isAnyWT(wirelessTerm)) {
				terminalList.add(Pair.of(false, Pair.of(i, wirelessTerm)));
			}
		}
		if (Mods.BAUBLES.isLoaded()) {
			Set<Pair<Integer, ItemStack>> wctBaubles = getBaublesUtility().getAllWTBaubles(player);
			if (!wctBaubles.isEmpty()) {
				for (Pair<Integer, ItemStack> currentPair : wctBaubles) {
					ItemStack wctBauble = currentPair.getRight();
					if (isAnyWT(wctBauble)) {
						terminalList.add(Pair.of(true, Pair.of(currentPair.getLeft(), wctBauble)));
					}
				}
			}
		}
		return terminalList;
	}

	// get a specific type of wireless terminal
	@Override
	public Set<Pair<Boolean, Pair<Integer, ItemStack>>> getAllWirelessTerminalsByType(EntityPlayer player, Class<? extends ICustomWirelessTerminalItem> type) {
		Set<Pair<Boolean, Pair<Integer, ItemStack>>> typeTerminals = Sets.newHashSet();
		Set<Pair<Boolean, Pair<Integer, ItemStack>>> terminals = getAllWirelessTerminals(player);
		for (Pair<Boolean, Pair<Integer, ItemStack>> terminal : terminals) {
			ItemStack currentTerminalStack = terminal.getRight().getRight();
			Class<?> clazz = currentTerminalStack.getItem().getClass();
			Set<Class<?>> applicableInterfaces = Sets.newHashSet(ClassUtils.getAllInterfaces(clazz));
			if (!currentTerminalStack.isEmpty() && applicableInterfaces.contains(type)) {
				typeTerminals.add(terminal);
			}
		}
		return typeTerminals;
	}

	@Override
	public ItemStack getWTBySlot(EntityPlayer player, int slot, Class<? extends ICustomWirelessTerminalItem> type) {
		return getWTBySlot(player, false, slot, type);
	}

	@Override
	public ItemStack getWTBySlot(EntityPlayer player, boolean isBauble, int slot, Class<? extends ICustomWirelessTerminalItem> type) {
		if (isBauble) {
			return getBaublesUtility().getWTBySlot(player, slot, type);
		}
		ItemStack wirelessTerminal = player.inventory.mainInventory.get(slot);
		if (!wirelessTerminal.isEmpty()) {
			Class<?> clazz = wirelessTerminal.getItem().getClass();
			Class<?>[] interfaces = clazz.getInterfaces();
			if (interfaces.length <= 0) {
				// this should only happen for creative versions
				clazz = clazz.getSuperclass();
				interfaces = clazz.getInterfaces();
			}
			List<Class<?>> applicableInterfaces = Lists.newArrayList(interfaces);
			if (!wirelessTerminal.isEmpty() && applicableInterfaces.contains(type)) {
				return wirelessTerminal;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getWTBySlot(EntityPlayer player, int slot) {
		ItemStack wirelessTerminal = player.inventory.mainInventory.get(slot);
		if (!wirelessTerminal.isEmpty() && wirelessTerminal.getItem() instanceof ICustomWirelessTerminalItem) {
			return wirelessTerminal;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getFirstWirelessTerminal(EntityPlayer player) {
		Set<Pair<Integer, ItemStack>> wirelessTerms = getWirelessTerminals(player);
		if (!wirelessTerms.isEmpty()) {
			return wirelessTerms.stream().findFirst().get().getRight();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean shouldConsumeBoosters(ItemStack wirelessTerminal) {
		if (!LibConfig.USE_OLD_INFINTY_MECHANIC && wirelessTerminal.hasTagCompound()) {
			if (wirelessTerminal.getTagCompound().hasKey(AUTOCONSUME_BOOSTER_NBT)) {
				boolean shouldConsume = wirelessTerminal.getTagCompound().getBoolean(AUTOCONSUME_BOOSTER_NBT);
				int currentCardCount = getInfinityEnergy(wirelessTerminal) / INFINITY_ENERGY_PER_BOOSTER_CARD;
				int maxCardCount = Integer.MAX_VALUE / INFINITY_ENERGY_PER_BOOSTER_CARD;
				return shouldConsume && maxCardCount > currentCardCount;
			}
		}
		return false;
	}

	@Override
	public boolean isBoosterInstalled(final ItemStack wirelessTerminal) {
		if (wirelessTerminal.hasTagCompound()) {
			NBTTagCompound boosterNBT = wirelessTerminal.getSubCompound(BOOSTER_SLOT_NBT);
			if (boosterNBT != null) {
				NBTTagList boosterNBTList = boosterNBT.getTagList("Items", 10);
				if (boosterNBTList != null) {
					NBTTagCompound boosterTagCompound = boosterNBTList.getCompoundTagAt(0);
					if (boosterTagCompound != null) {
						ItemStack boosterCard = new ItemStack(boosterTagCompound);
						if (boosterCard != null && !boosterCard.isEmpty()) {
							return ((boosterCard.getItem() instanceof ItemInfinityBooster) && LibConfig.WT_BOOSTER_ENABLED);
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void setInRange(ItemStack wirelessTerm, boolean value) {
		NBTTagCompound nbt = ensureTag(wirelessTerm);
		nbt.setBoolean(IN_RANGE_NBT, value);
		wirelessTerm.setTagCompound(nbt);
	}

	@Override
	public boolean isInRange(ItemStack wirelessTerm) {
		NBTTagCompound nbt = ensureTag(wirelessTerm);
		return (nbt.hasKey(IN_RANGE_NBT) && nbt.getBoolean(IN_RANGE_NBT)) || isWTCreative(wirelessTerm);
	}

	@Override
	public ItemStack addInfinityBoosters(@Nonnull ItemStack wirelessTerm, ItemStack boosterCardStack) {
		int currentCardCount = getInfinityEnergy(wirelessTerm) / INFINITY_ENERGY_PER_BOOSTER_CARD;
		int maxCardCount = Integer.MAX_VALUE / INFINITY_ENERGY_PER_BOOSTER_CARD;
		if (currentCardCount < maxCardCount) {
			int spaceAvailable = maxCardCount - currentCardCount;
			int numberOfCardsTryingToAdd = boosterCardStack.getCount();
			if (spaceAvailable > 0 && numberOfCardsTryingToAdd > 0) { //can we at least add 1 card?
				int cardsTryingToAdd = numberOfCardsTryingToAdd;
				if (cardsTryingToAdd <= spaceAvailable) {
					setInfinityEnergy(wirelessTerm, (cardsTryingToAdd * INFINITY_ENERGY_PER_BOOSTER_CARD) + getInfinityEnergy(wirelessTerm));
					if (cardsTryingToAdd == spaceAvailable) {
						boosterCardStack = ItemStack.EMPTY;
					}
					else {
						boosterCardStack.setCount(cardsTryingToAdd - spaceAvailable);
					}
				}
			}
		}
		return boosterCardStack;
	}

	@Override
	public boolean hasInfiniteRange(@Nonnull ItemStack wirelessTerm) {
		if (LibConfig.USE_OLD_INFINTY_MECHANIC) {
			return isBoosterInstalled(wirelessTerm) || isWTCreative(wirelessTerm);
		}
		else {
			return hasInfinityEnergy(wirelessTerm);
		}
	}

	@Override
	public boolean hasInfinityEnergy(@Nonnull ItemStack wirelessTerm) {
		if (ensureTag(wirelessTerm).hasKey(INFINITY_ENERGY_NBT)) {
			return getInfinityEnergy(wirelessTerm) > 0 && LibConfig.WT_BOOSTER_ENABLED;
		}
		return isWTCreative(wirelessTerm);
	}

	@Override
	public boolean isAnyWT(@Nonnull ItemStack wirelessTerm) {
		return wirelessTerm.getItem() instanceof ICustomWirelessTerminalItem;
	}

	@Override
	public boolean isInRangeOfWAP(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player) {
		return getDistanceToWAP(wirelessTerm, player) <= getWAPRange(wirelessTerm, player) && getWAPRange(wirelessTerm, player) != Double.MAX_VALUE;
	}

	@Override
	public double getDistanceToWAP(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player) {
		IWirelessAccessPoint wap = getClosestWAPToPlayer(wirelessTerm, player);
		if (wap != null && player.getEntityWorld().provider.getDimension() == wap.getLocation().getWorld().provider.getDimension()) {
			BlockPos wapPos = wap.getLocation().getPos();
			BlockPos playerPos = player.getPosition();
			double distanceToWap = Math.sqrt(playerPos.distanceSq(wapPos));
			return distanceToWap;
		}
		return Double.MAX_VALUE;
	}

	@Override
	public double getWAPRange(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player) {
		IWirelessAccessPoint wap = getClosestWAPToPlayer(wirelessTerm, player);
		if (wap != null) {
			return wap.getRange();
		}
		return Double.MAX_VALUE;
	}

	@Override
	public IWirelessAccessPoint getClosestWAPToPlayer(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player) {
		Set<IWirelessAccessPoint> wapList = getWAPs(wirelessTerm, player);
		double closestDistance = -1.0D;
		IWirelessAccessPoint closestWAP = null;
		for (IWirelessAccessPoint wap : wapList) {
			BlockPos wapPos = wap.getLocation().getPos();
			BlockPos playerPos = player.getPosition();
			double thisWAPDistance = Math.sqrt(playerPos.distanceSq(wapPos));
			if (closestDistance == -1.0D) {
				closestDistance = thisWAPDistance;
				closestWAP = wap;
			}
			else {
				if (thisWAPDistance < closestDistance) {
					closestDistance = thisWAPDistance;
					closestWAP = wap;
				}
			}
		}
		return closestDistance == -1.0D ? null : closestWAP;
	}

	@Override
	public Set<IWirelessAccessPoint> getWAPs(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player) {
		if (isAnyWT(wirelessTerm)) {
			WTGuiObject<?> object = getGUIObject(wirelessTerm, player);
			if (object != null) {
				return Sets.newHashSet(object.getWAPs());
			}
		}
		return new HashSet<IWirelessAccessPoint>();
	}

	@Override
	public WTGuiObject<?> getGUIObject(EntityPlayer player) {
		return getGUIObject(null, player);
	}

	@Override
	public WTGuiObject<?> getGUIObject(@Nullable ItemStack wirelessTerm, @Nonnull EntityPlayer player) {
		if (wirelessTerm == null) {
			if (player.openContainer instanceof ContainerWT) {
				ContainerWT c = (ContainerWT) player.openContainer;
				if (c.getGuiObject() != null) {
					return c.getGuiObject();
				}
			}
		}
		else {
			if (wirelessTerm.getItem() instanceof ICustomWirelessTermHandler) {
				if (player != null && player.getEntityWorld() != null) {
					ICustomWirelessTermHandler wth = (ICustomWirelessTermHandler) wirelessTerm.getItem();
					return getGUIObject(wth, wirelessTerm, player);
				}
			}
		}
		return null;
	}

	@Override
	public WTGuiObject<?> getGUIObject(ICustomWirelessTermHandler wth, @Nonnull ItemStack wirelessTerm, EntityPlayer player) {
		return new WTGuiObjectImpl<>(wth, wirelessTerm, player, player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
	}

	@Override
	public void setInfinityEnergy(@Nonnull ItemStack wirelessTerm, int amount) {
		if (!isWTCreative(wirelessTerm)) {
			NBTTagCompound nbt = ensureTag(wirelessTerm);
			nbt.setInteger(INFINITY_ENERGY_NBT, amount);
			wirelessTerm.setTagCompound(nbt);
		}
	}

	@Override
	public int getInfinityEnergy(@Nonnull ItemStack wirelessTerm) {
		NBTTagCompound nbt = ensureTag(wirelessTerm);
		if (!nbt.hasKey(INFINITY_ENERGY_NBT) && !isWTCreative(wirelessTerm)) {
			nbt.setInteger(INFINITY_ENERGY_NBT, 0);
		}
		return isWTCreative(wirelessTerm) ? Integer.MAX_VALUE : nbt.getInteger(INFINITY_ENERGY_NBT);
	}

	@Override
	public void drainInfinityEnergy(@Nonnull ItemStack wirelessTerm, EntityPlayer player, boolean isBauble, int slot) {
		if (player instanceof EntityPlayerMP) {
			if (!LibConfig.USE_OLD_INFINTY_MECHANIC && !isWTCreative(wirelessTerm)) {
				int current = getInfinityEnergy(wirelessTerm);
				if (!isInRangeOfWAP(wirelessTerm, player)) {
					int reducedAmount = current - INFINITY_ENERGY_DRAIN;
					if (reducedAmount < 0) {
						reducedAmount = 0;
					}
					setInfinityEnergy(wirelessTerm, reducedAmount);
					LibNetworking.instance().sendTo(new PacketSyncInfinityEnergy(getInfinityEnergy(wirelessTerm), player.getUniqueID(), isBauble, slot), (EntityPlayerMP) player);
				}
			}
		}
	}

	@Override
	public boolean isWTCreative(ItemStack wirelessTerm) {
		return !wirelessTerm.isEmpty() && wirelessTerm.getItem() instanceof ICustomWirelessTerminalItem && ((ICustomWirelessTerminalItem) wirelessTerm.getItem()).isCreative();
	}

	@Override
	public NBTTagCompound ensureTag(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String color(String color) {
		switch (color) {
		case "white":
			return TextFormatting.WHITE.toString();
		case "black":
			return TextFormatting.BLACK.toString();
		case "green":
			return TextFormatting.GREEN.toString();
		case "red":
			return TextFormatting.RED.toString();
		case "yellow":
			return TextFormatting.YELLOW.toString();
		case "aqua":
			return TextFormatting.AQUA.toString();
		case "blue":
			return TextFormatting.BLUE.toString();
		case "italics":
			return TextFormatting.ITALIC.toString();
		case "bold":
			return TextFormatting.BOLD.toString();
		default:
		case "gray":
			return TextFormatting.GRAY.toString();
		}
	}

	public static class WTConfigImpl extends WTConfig {

		@Override
		public boolean isInfinityBoosterCardEnabled() {
			return LibConfig.WT_BOOSTER_ENABLED;
		}

		@Override
		public boolean isOldInfinityMechanicEnabled() {
			return LibConfig.USE_OLD_INFINTY_MECHANIC;
		}

		@Override
		public int getLowInfinityEnergyWarningAmount() {
			return LibConfig.INFINTY_ENERGY_LOW_WARNING_AMOUNT;
		}

		@Override
		public boolean shiftClickBaublesEnabled() {
			return LibConfig.SHIFT_CLICK_BAUBLES;
		}

		@Override
		public int getWTMaxPower() {
			return LibConfig.WT_MAX_POWER;
		}

		@Override
		public String getConfigFile() {
			return LibGlobals.CONFIG_FILE;
		}

	}

	@Override
	public AppEngSlot createOldBoosterSlot(IItemHandler inventory, int xPos, int yPos) {
		return new SlotBooster(inventory, xPos, yPos);
	}

	@Override
	public AppEngInternalInventory createBoosterInventory(IAEAppEngInventory inventory) {
		return new WTInventoryBooster(inventory);
	}

	@Override
	public AppEngSlot createInfinityBoosterSlot(int posX, int posY) {
		return new SlotBoosterEnergy(posX, posY);
	}

	@Override
	public AppEngSlot createNullSlot() {
		return new NullSlot();
	}

	@Override
	public AppEngSlot createArmorSlot(EntityPlayer player, IItemHandler inventory, int slot, int posX, int posY, EntityEquipmentSlot armorSlot) {
		return new SlotArmor(player, inventory, slot, posX, posY, armorSlot);
	}

	@Override
	public AppEngSlot createTrashSlot(IItemHandler inventory, int posX, int posY) {
		return new SlotTrash(inventory, posX, posY);
	}

	@Override
	public IWTGuiScrollbar createScrollbar() {
		return new GuiScrollbar();
	}

}