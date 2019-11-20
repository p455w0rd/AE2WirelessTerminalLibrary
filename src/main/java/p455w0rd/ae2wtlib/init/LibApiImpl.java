package p455w0rd.ae2wtlib.init;

import static p455w0rd.ae2wtlib.init.LibConfig.INFINITY_ENERGY_DRAIN;
import static p455w0rd.ae2wtlib.init.LibConfig.INFINITY_ENERGY_PER_BOOSTER_CARD;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import p455w0rd.ae2wtlib.AE2WTLib;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.ae2wtlib.api.client.IWTGuiScrollbar;
import p455w0rd.ae2wtlib.api.client.gui.widgets.GuiScrollbar;
import p455w0rd.ae2wtlib.api.container.ContainerWT;
import p455w0rd.ae2wtlib.api.container.slot.*;
import p455w0rd.ae2wtlib.api.inventory.WTInventoryBooster;
import p455w0rd.ae2wtlib.helpers.IWirelessUniversalItem;
import p455w0rd.ae2wtlib.helpers.WTGuiObjectImpl;
import p455w0rd.ae2wtlib.integration.Baubles;
import p455w0rd.ae2wtlib.items.ItemInfinityBooster;
import p455w0rd.ae2wtlib.items.ItemWUT;
import p455w0rd.ae2wtlib.sync.packets.PacketSyncInfinityEnergy;
import p455w0rdslib.LibGlobals.Mods;

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
	private static WUTUtility WUT_UTILITY = null;

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
	public WUTUtility getWUTUtility() {
		if (WUT_UTILITY == null) {
			if (!LibApiImpl.hasFinishedPreInit()) {
				return null;
			}
			WUT_UTILITY = WUTUtilityImpl.getInstance();
		}
		return WUT_UTILITY;
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
	public WTRegistry getWirelessTerminalRegistry() {
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
	public Set<Pair<Integer, ItemStack>> getWirelessTerminals(final EntityPlayer player) {
		return getWirelessTerminals(player, false);
	}

	@Override
	public Set<Pair<Integer, ItemStack>> getWirelessTerminals(final EntityPlayer player, final boolean isBauble) {
		if (isBauble) {
			return getBaublesUtility().getAllWTBaubles(player);
		}
		final Set<Pair<Integer, ItemStack>> terminalList = Sets.newHashSet();
		final NonNullList<ItemStack> playerInventory = player.inventory.mainInventory;
		for (int i = 0; i < playerInventory.size(); i++) {
			final ItemStack wirelessTerm = playerInventory.get(i);
			if (isAnyWT(wirelessTerm)) {
				terminalList.add(Pair.of(i, wirelessTerm));
			}
		}
		if (Mods.BAUBLES.isLoaded()) {
			final Set<Pair<Integer, ItemStack>> wtBaubles = getBaublesUtility().getAllWTBaubles(player);
			if (!wtBaubles.isEmpty()) {
				terminalList.addAll(wtBaubles);
			}
		}
		return terminalList;
	}

	// Parent pair contains a boolean which tells whether or not this is a bauble slot
	// Child pair gives the slot number and ItemStack
	@Override
	public Set<Pair<Boolean, Pair<Integer, ItemStack>>> getAllWirelessTerminals(final EntityPlayer player) {
		final Set<Pair<Boolean, Pair<Integer, ItemStack>>> terminalList = Sets.newHashSet();
		final NonNullList<ItemStack> playerInventory = player.inventory.mainInventory;
		for (int i = 0; i < playerInventory.size(); i++) {
			final ItemStack wirelessTerm = playerInventory.get(i);
			if (isAnyWT(wirelessTerm)) {
				terminalList.add(Pair.of(false, Pair.of(i, wirelessTerm)));
			}
		}
		if (Mods.BAUBLES.isLoaded()) {
			final Set<Pair<Integer, ItemStack>> wctBaubles = getBaublesUtility().getAllWTBaubles(player);
			if (!wctBaubles.isEmpty()) {
				for (final Pair<Integer, ItemStack> currentPair : wctBaubles) {
					final ItemStack wctBauble = currentPair.getRight();
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
	public Set<Pair<Boolean, Pair<Integer, ItemStack>>> getAllWirelessTerminalsByType(final EntityPlayer player, final Class<? extends ICustomWirelessTerminalItem> type) {
		final Set<Pair<Boolean, Pair<Integer, ItemStack>>> typeTerminals = Sets.newHashSet();
		final Set<Pair<Boolean, Pair<Integer, ItemStack>>> terminals = getAllWirelessTerminals(player);
		for (final Pair<Boolean, Pair<Integer, ItemStack>> terminal : terminals) {
			final ItemStack currentTerminalStack = terminal.getRight().getRight();
			final Class<?> clazz = currentTerminalStack.getItem().getClass();
			final Set<Class<?>> applicableInterfaces = Sets.newHashSet(ClassUtils.getAllInterfaces(clazz));
			if (!currentTerminalStack.isEmpty() && applicableInterfaces.contains(type) || getWUTUtility().doesWUTSupportType(currentTerminalStack, type)) {
				typeTerminals.add(terminal);
			}
		}
		return typeTerminals;
	}

	@Override
	public boolean containsCreativeTerminal(final ICustomWirelessTerminalItem... wirelessTerminals) {
		for (final ICustomWirelessTerminalItem terminal : wirelessTerminals) {
			if (terminal.isCreative()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getWTBySlot(final EntityPlayer player, final int slot, final Class<? extends ICustomWirelessTerminalItem> type) {
		return getWTBySlot(player, false, slot, type);
	}

	@Override
	public ItemStack getWTBySlot(final EntityPlayer player, final boolean isBauble, final int slot, final Class<? extends ICustomWirelessTerminalItem> type) {
		if (isBauble) {
			return getBaublesUtility().getWTBySlot(player, slot, type);
		}
		final ItemStack wirelessTerminal = player.inventory.getStackInSlot(slot);
		if (!wirelessTerminal.isEmpty()) {
			final List<Class<?>> applicableInterfaces = ClassUtils.getAllInterfaces(wirelessTerminal.getItem().getClass());
			if (getWUTUtility().isWUT(wirelessTerminal)) {
				for (final Pair<ICustomWirelessTerminalItem, Integer> currentPair : getWUTUtility().getStoredTerminalHandlers(wirelessTerminal)) {
					final List<Class<?>> storedInterfaces = ClassUtils.getAllInterfaces(currentPair.getLeft().getClass());
					for (final Class<?> currentInterface : storedInterfaces) {
						if (!applicableInterfaces.contains(currentInterface)) {
							applicableInterfaces.add(currentInterface);
						}
					}
				}
			}
			if (!wirelessTerminal.isEmpty() && (applicableInterfaces.contains(type) || getWUTUtility().doesWUTSupportType(wirelessTerminal, type))) {
				return wirelessTerminal;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getWTBySlot(final EntityPlayer player, final int slot) {
		return getWTBySlot(player, false, slot);
	}

	@Override
	public ItemStack getWTBySlot(final EntityPlayer player, final boolean isBauble, final int slot) {
		if (isBauble) {
			return getBaublesUtility().getWTBySlot(player, slot, ICustomWirelessTerminalItem.class);
		}
		final ItemStack wirelessTerminal = player.inventory.getStackInSlot(slot);
		if (!wirelessTerminal.isEmpty() && wirelessTerminal.getItem() instanceof ICustomWirelessTerminalItem) {
			return wirelessTerminal;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getFirstWirelessTerminal(final EntityPlayer player) {
		final Set<Pair<Integer, ItemStack>> wirelessTerms = getWirelessTerminals(player);
		if (!wirelessTerms.isEmpty()) {
			return wirelessTerms.stream().findFirst().get().getRight();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean shouldConsumeBoosters(final ItemStack wirelessTerminal) {
		if (!LibConfig.USE_OLD_INFINTY_MECHANIC && wirelessTerminal.hasTagCompound()) {
			if (wirelessTerminal.getTagCompound().hasKey(WTApi.instance().getConstants().getNBTTagNames().autoConsumeBooster())) {
				final boolean shouldConsume = wirelessTerminal.getTagCompound().getBoolean(WTApi.instance().getConstants().getNBTTagNames().autoConsumeBooster());
				final int currentCardCount = getInfinityEnergy(wirelessTerminal) / INFINITY_ENERGY_PER_BOOSTER_CARD;
				final int maxCardCount = Integer.MAX_VALUE / INFINITY_ENERGY_PER_BOOSTER_CARD;
				return shouldConsume && maxCardCount > currentCardCount;
			}
		}
		return false;
	}

	@Override
	public boolean isBoosterInstalled(final ItemStack wirelessTerminal) {
		if (wirelessTerminal.hasTagCompound()) {
			final NBTTagCompound boosterNBT = wirelessTerminal.getSubCompound(WTApi.instance().getConstants().getNBTTagNames().boosterSlot());
			if (boosterNBT != null) {
				final NBTTagList boosterNBTList = boosterNBT.getTagList("Items", 10);
				if (boosterNBTList != null) {
					final NBTTagCompound boosterTagCompound = boosterNBTList.getCompoundTagAt(0);
					if (boosterTagCompound != null) {
						final ItemStack boosterCard = new ItemStack(boosterTagCompound);
						if (boosterCard != null && !boosterCard.isEmpty()) {
							return boosterCard.getItem() instanceof ItemInfinityBooster && LibConfig.WT_BOOSTER_ENABLED;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void setInRange(final ItemStack wirelessTerm, final boolean value) {
		final NBTTagCompound nbt = ensureTag(wirelessTerm);
		nbt.setBoolean(WTApi.instance().getConstants().getNBTTagNames().inRange(), value);
		wirelessTerm.setTagCompound(nbt);
	}

	@Override
	public boolean isInRange(final ItemStack wirelessTerm) {
		final NBTTagCompound nbt = ensureTag(wirelessTerm);
		return nbt.hasKey(WTApi.instance().getConstants().getNBTTagNames().inRange()) && nbt.getBoolean(WTApi.instance().getConstants().getNBTTagNames().inRange()) || isWTCreative(wirelessTerm);
	}

	@Override
	public ItemStack addInfinityBoosters(@Nonnull final ItemStack wirelessTerm, ItemStack boosterCardStack) {
		final int currentCardCount = getInfinityEnergy(wirelessTerm) / INFINITY_ENERGY_PER_BOOSTER_CARD;
		final int maxCardCount = Integer.MAX_VALUE / INFINITY_ENERGY_PER_BOOSTER_CARD;
		if (currentCardCount < maxCardCount) {
			final int spaceAvailable = maxCardCount - currentCardCount;
			final int numberOfCardsTryingToAdd = boosterCardStack.getCount();
			if (spaceAvailable > 0 && numberOfCardsTryingToAdd > 0) { //can we at least add 1 card?
				final int cardsTryingToAdd = numberOfCardsTryingToAdd;
				if (cardsTryingToAdd <= spaceAvailable) {
					setInfinityEnergy(wirelessTerm, cardsTryingToAdd * INFINITY_ENERGY_PER_BOOSTER_CARD + getInfinityEnergy(wirelessTerm));
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
	public boolean hasInfiniteRange(@Nonnull final ItemStack wirelessTerm) {
		if (LibConfig.USE_OLD_INFINTY_MECHANIC) {
			return isBoosterInstalled(wirelessTerm) || isWTCreative(wirelessTerm);
		}
		else {
			return hasInfinityEnergy(wirelessTerm);
		}
	}

	@Override
	public boolean hasInfinityEnergy(@Nonnull final ItemStack wirelessTerm) {
		if (ensureTag(wirelessTerm).hasKey(WTApi.instance().getConstants().getNBTTagNames().infinityEnergy())) {
			return getInfinityEnergy(wirelessTerm) > 0 && LibConfig.WT_BOOSTER_ENABLED;
		}
		return isWTCreative(wirelessTerm);
	}

	@Override
	public boolean isAnyWT(@Nonnull final ItemStack wirelessTerm) {
		return wirelessTerm.getItem() instanceof ICustomWirelessTerminalItem;
	}

	@Override
	public boolean isInRangeOfWAP(@Nonnull final ItemStack wirelessTerm, @Nonnull final EntityPlayer player) {
		return getDistanceToWAP(wirelessTerm, player) <= getWAPRange(wirelessTerm, player) && getWAPRange(wirelessTerm, player) != Double.MAX_VALUE;
	}

	@Override
	public double getDistanceToWAP(@Nonnull final ItemStack wirelessTerm, @Nonnull final EntityPlayer player) {
		final IWirelessAccessPoint wap = getClosestWAPToPlayer(wirelessTerm, player);
		if (wap != null && player.getEntityWorld().provider.getDimension() == wap.getLocation().getWorld().provider.getDimension()) {
			final BlockPos wapPos = wap.getLocation().getPos();
			final BlockPos playerPos = player.getPosition();
			final double distanceToWap = Math.sqrt(playerPos.distanceSq(wapPos));
			return distanceToWap;
		}
		return Double.MAX_VALUE;
	}

	@Override
	public double getWAPRange(@Nonnull final ItemStack wirelessTerm, @Nonnull final EntityPlayer player) {
		final IWirelessAccessPoint wap = getClosestWAPToPlayer(wirelessTerm, player);
		if (wap != null) {
			return wap.getRange();
		}
		return Double.MAX_VALUE;
	}

	@Override
	public IWirelessAccessPoint getClosestWAPToPlayer(@Nonnull final ItemStack wirelessTerm, @Nonnull final EntityPlayer player) {
		final Set<IWirelessAccessPoint> wapList = getWAPs(wirelessTerm, player);
		double closestDistance = -1.0D;
		IWirelessAccessPoint closestWAP = null;
		for (final IWirelessAccessPoint wap : wapList) {
			final BlockPos wapPos = wap.getLocation().getPos();
			final BlockPos playerPos = player.getPosition();
			final double thisWAPDistance = Math.sqrt(playerPos.distanceSq(wapPos));
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
	public Set<IWirelessAccessPoint> getWAPs(@Nonnull final ItemStack wirelessTerm, @Nonnull final EntityPlayer player) {
		if (isAnyWT(wirelessTerm)) {
			final WTGuiObject<?> object = getGUIObject(wirelessTerm, player);
			if (object != null) {
				return Sets.newHashSet(object.getWAPs());
			}
		}
		return new HashSet<>();
	}

	@Override
	public WTGuiObject<?> getGUIObject(final EntityPlayer player) {
		return getGUIObject(null, player);
	}

	@Override
	public WTGuiObject<?> getGUIObject(@Nullable final ItemStack wirelessTerm, @Nonnull final EntityPlayer player) {
		if (wirelessTerm == null) {
			if (player.openContainer instanceof ContainerWT) {
				final ContainerWT c = (ContainerWT) player.openContainer;
				if (c.getGuiObject() != null) {
					return c.getGuiObject();
				}
			}
		}
		else {
			if (wirelessTerm.getItem() instanceof ICustomWirelessTerminalItem) {
				if (player != null && player.getEntityWorld() != null) {
					final ICustomWirelessTerminalItem wth = (ICustomWirelessTerminalItem) wirelessTerm.getItem();
					return getGUIObject(wth, wirelessTerm, player);
				}
			}
		}
		return null;
	}

	@Override
	public WTGuiObject<?> getGUIObject(final ICustomWirelessTerminalItem wth, @Nonnull final ItemStack wirelessTerm, final EntityPlayer player) {
		return new WTGuiObjectImpl<>(wth, wirelessTerm, player, player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
	}

	@Override
	public void setInfinityEnergy(@Nonnull final ItemStack wirelessTerm, final int amount) {
		if (!isWTCreative(wirelessTerm)) {
			final NBTTagCompound nbt = ensureTag(wirelessTerm);
			nbt.setInteger(WTApi.instance().getConstants().getNBTTagNames().infinityEnergy(), amount);
			wirelessTerm.setTagCompound(nbt);
		}
	}

	@Override
	public int getInfinityEnergy(@Nonnull final ItemStack wirelessTerm) {
		final NBTTagCompound nbt = ensureTag(wirelessTerm);
		if (!nbt.hasKey(WTApi.instance().getConstants().getNBTTagNames().infinityEnergy()) && !isWTCreative(wirelessTerm)) {
			nbt.setInteger(WTApi.instance().getConstants().getNBTTagNames().infinityEnergy(), 0);
		}
		return isWTCreative(wirelessTerm) ? Integer.MAX_VALUE : nbt.getInteger(WTApi.instance().getConstants().getNBTTagNames().infinityEnergy());
	}

	@Override
	public void drainInfinityEnergy(@Nonnull final ItemStack wirelessTerm, final EntityPlayer player, final boolean isBauble, final int slot) {
		if (player instanceof EntityPlayerMP) {
			if (!LibConfig.USE_OLD_INFINTY_MECHANIC && !isWTCreative(wirelessTerm)) {
				final int current = getInfinityEnergy(wirelessTerm);
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
	public boolean isWTCreative(final ItemStack wirelessTerm) {
		return !wirelessTerm.isEmpty() && wirelessTerm.getItem() instanceof ICustomWirelessTerminalItem && ((ICustomWirelessTerminalItem) wirelessTerm.getItem()).isCreative();
	}

	@Override
	public NBTTagCompound ensureTag(final ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound();
	}

	@Override
	public boolean isTerminalLinked(final ItemStack wirelessTerminal) {
		String sourceKey = "";
		if (wirelessTerminal.getItem() instanceof ICustomWirelessTerminalItem && wirelessTerminal.hasTagCompound()) {
			sourceKey = ((ICustomWirelessTerminalItem) wirelessTerminal.getItem()).getEncryptionKey(wirelessTerminal);
			return sourceKey != null && !sourceKey.isEmpty();
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String color(final String color) {
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
	public AppEngSlot createOldBoosterSlot(final IItemHandler inventory, final int xPos, final int yPos) {
		return new SlotBooster(inventory, xPos, yPos);
	}

	@Override
	public AppEngInternalInventory createBoosterInventory(final IAEAppEngInventory inventory) {
		return new WTInventoryBooster(inventory);
	}

	@Override
	public AppEngSlot createInfinityBoosterSlot(final int posX, final int posY) {
		return new SlotBoosterEnergy(posX, posY);
	}

	@Override
	public AppEngSlot createNullSlot() {
		return new NullSlot();
	}

	@Override
	public AppEngSlot createArmorSlot(final EntityPlayer player, final IItemHandler inventory, final int slot, final int posX, final int posY, final EntityEquipmentSlot armorSlot) {
		return new SlotArmor(player, inventory, slot, posX, posY, armorSlot);
	}

	@Override
	public AppEngSlot createTrashSlot(final IItemHandler inventory, final int posX, final int posY) {
		return new SlotTrash(inventory, posX, posY);
	}

	@Override
	public IWTGuiScrollbar createScrollbar() {
		return new GuiScrollbar();
	}

	public static class WUTUtilityImpl extends WUTUtility {

		private static WUTUtilityImpl INSTANCE = new WUTUtilityImpl();

		private WUTUtilityImpl() {
		}

		public static WUTUtility getInstance() {
			return INSTANCE;
		}

		@Override
		public boolean doesWUTSupportType(final ItemStack wut, final Class<?> type) {
			return ItemWUT.isTypeInstalled(wut, type);
		}

		@Override
		public Pair<ItemStack, Integer> getSelectedTerminal(final ItemStack wut) {
			return ItemWUT.getSelectedTerminalStack(wut);
		}

		@Override
		public boolean isWUT(final ItemStack stack) {
			return stack.getItem() instanceof IWirelessUniversalItem;
		}

		@Override
		public ResourceLocation[] getMenuIcons(final ItemStack wut) {
			return ItemWUT.getMenuIcons(wut);
		}

		@Override
		public List<Pair<ItemStack, Integer>> getStoredTerminals(final ItemStack wut) {
			return ItemWUT.getStoredTerminalStacks(wut);
		}

		@Override
		public List<Pair<ICustomWirelessTerminalItem, Integer>> getStoredTerminalHandlers(final ItemStack wut) {
			return ItemWUT.getStoredTerminalHandlers(wut);
		}

	}

}