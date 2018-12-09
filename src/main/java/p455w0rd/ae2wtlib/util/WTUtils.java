
package p455w0rd.ae2wtlib.util;

import static p455w0rd.ae2wtlib.init.LibConfig.INFINITY_ENERGY_DRAIN;
import static p455w0rd.ae2wtlib.init.LibConfig.INFINITY_ENERGY_PER_BOOSTER_CARD;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import appeng.api.implementations.tiles.IWirelessAccessPoint;
import baubles.api.cap.BaublesCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.api.ICustomWirelessTermHandler;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.container.ContainerWT;
import p455w0rd.ae2wtlib.helpers.WTGuiObject;
import p455w0rd.ae2wtlib.init.LibConfig;
import p455w0rd.ae2wtlib.init.LibIntegration.Mods;
import p455w0rd.ae2wtlib.init.LibNetworking;
import p455w0rd.ae2wtlib.integration.Baubles;
import p455w0rd.ae2wtlib.items.ItemInfinityBooster;
import p455w0rd.ae2wtlib.sync.packets.PacketSyncInfinityEnergyInv;

public class WTUtils {

	public static final String INFINITY_ENERGY_NBT = "InfinityEnergy";
	public static final String BOOSTER_SLOT_NBT = "BoosterSlot";
	public static final String IN_RANGE_NBT = "IsInRange";
	public static final String AUTOCONSUME_BOOSTER_NBT = "AutoConsumeBoosters";

	public static List<Pair<Integer, ItemStack>> getWirelessTerminals(EntityPlayer player) {
		return getWirelessTerminals(player, false);
	}

	public static List<Pair<Integer, ItemStack>> getWirelessTerminals(EntityPlayer player, boolean isBauble) {
		if (isBauble) {
			return Baubles.getAllWTBaubles(player);
		}
		List<Pair<Integer, ItemStack>> terminalList = Lists.newArrayList();
		InventoryPlayer playerInventory = player.inventory;
		for (int i = 0; i < playerInventory.getSizeInventory(); i++) {
			ItemStack wirelessTerm = playerInventory.getStackInSlot(i);
			if (isAnyWT(wirelessTerm)) {
				terminalList.add(Pair.of(i, wirelessTerm));
			}
		}
		if (Mods.BAUBLES.isLoaded()) {
			List<Pair<Integer, ItemStack>> wtBaubles = Baubles.getAllWTBaubles(player);
			if (!wtBaubles.isEmpty()) {
				terminalList.addAll(wtBaubles);
			}
		}
		return terminalList;
	}

	// Parent pair contains a boolean which tells whether or not this is a bauble slot
	// Child pair gives the slot number and ItemStack
	public static List<Pair<Boolean, Pair<Integer, ItemStack>>> getAllWirelessTerminals(EntityPlayer player) {
		List<Pair<Boolean, Pair<Integer, ItemStack>>> terminalList = Lists.newArrayList();
		InventoryPlayer inventory = player.inventory;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack wirelessTerm = inventory.getStackInSlot(i);
			if (isAnyWT(wirelessTerm)) {
				terminalList.add(Pair.of(false, Pair.of(i, wirelessTerm)));
			}
		}
		if (Mods.BAUBLES.isLoaded()) {
			List<Pair<Integer, ItemStack>> wctBaubles = Baubles.getAllWTBaubles(player);
			if (!wctBaubles.isEmpty()) {
				for (int i = 0; i < wctBaubles.size(); i++) {
					ItemStack wctBauble = wctBaubles.get(i).getRight();
					if (isAnyWT(wctBauble)) {
						terminalList.add(Pair.of(true, Pair.of(wctBaubles.get(i).getLeft(), wctBauble)));
					}
				}
			}
		}
		return terminalList;
	}

	// get a specific type of wireless terminal
	public static List<Pair<Boolean, Pair<Integer, ItemStack>>> getAllWirelessTerminalsByType(EntityPlayer player, Class<? extends ICustomWirelessTerminalItem> type) {
		List<Pair<Boolean, Pair<Integer, ItemStack>>> typeTerminals = Lists.newArrayList();
		List<Pair<Boolean, Pair<Integer, ItemStack>>> terminals = getAllWirelessTerminals(player);
		for (Pair<Boolean, Pair<Integer, ItemStack>> terminal : terminals) {
			ItemStack currentTerminalStack = terminal.getRight().getRight();
			Class<?> clazz = currentTerminalStack.getItem().getClass();
			Class<?>[] interfaces = clazz.getInterfaces();
			if (interfaces.length <= 0) {
				// this should only happen for creative versions
				clazz = clazz.getSuperclass();
				interfaces = clazz.getInterfaces();
			}
			List<Class<?>> applicableInterfaces = Lists.newArrayList(interfaces);
			if (!currentTerminalStack.isEmpty() && applicableInterfaces.contains(type)) {
				typeTerminals.add(terminal);
			}
		}
		return typeTerminals;
	}

	public static ItemStack getWTBySlot(EntityPlayer player, int slot, Class<? extends ICustomWirelessTerminalItem> type) {
		return getWTBySlot(player, false, slot, type);
	}

	public static ItemStack getWTBySlot(EntityPlayer player, boolean isBauble, int slot, Class<? extends ICustomWirelessTerminalItem> type) {
		if (isBauble) {
			return Baubles.getWTBySlot(player, slot, type);
		}
		if (player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)) {
			ItemStack wirelessTerminal = player.inventory.getStackInSlot(slot);
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

	public static ItemStack getWTBySlot(EntityPlayer player, int slot) {
		ItemStack wirelessTerminal = player.inventory.getStackInSlot(slot);
		if (!wirelessTerminal.isEmpty() && wirelessTerminal.getItem() instanceof ICustomWirelessTerminalItem) {
			return wirelessTerminal;
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack getFirstWirelessTerminal(EntityPlayer player) {
		List<Pair<Integer, ItemStack>> wirelessTerms = getWirelessTerminals(player);
		if (!wirelessTerms.isEmpty()) {
			return wirelessTerms.get(0).getRight();
		}
		return ItemStack.EMPTY;
	}

	public static boolean shouldConsumeBoosters(ItemStack wirelessTerminal) {
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

	public static boolean isBoosterInstalled(final ItemStack wirelessTerminal) {
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

	public static void setInRange(ItemStack wirelessTerm, boolean value) {
		NBTTagCompound nbt = ensureTag(wirelessTerm);
		nbt.setBoolean(IN_RANGE_NBT, value);
		wirelessTerm.setTagCompound(nbt);
	}

	public static boolean isInRange(ItemStack wirelessTerm) {
		NBTTagCompound nbt = ensureTag(wirelessTerm);
		return (nbt.hasKey(IN_RANGE_NBT) && nbt.getBoolean(IN_RANGE_NBT)) || isWTCreative(wirelessTerm);
	}

	public static ItemStack addInfinityBoosters(@Nonnull ItemStack wirelessTerm, ItemStack boosterCardStack) {
		int currentCardCount = getInfinityEnergy(wirelessTerm) / INFINITY_ENERGY_PER_BOOSTER_CARD;
		int maxCardCount = Integer.MAX_VALUE / INFINITY_ENERGY_PER_BOOSTER_CARD;
		if (currentCardCount < maxCardCount) {
			int spaceAvailable = maxCardCount - currentCardCount;
			int numberOfCardsTryingToAdd = boosterCardStack.getCount();
			if (spaceAvailable > 0 && numberOfCardsTryingToAdd > 0) { //can we at least add 1 card?
				int cardsTryingToAdd = numberOfCardsTryingToAdd * INFINITY_ENERGY_PER_BOOSTER_CARD;
				if (cardsTryingToAdd <= spaceAvailable) {
					setInfinityEnergy(wirelessTerm, cardsTryingToAdd + currentCardCount * INFINITY_ENERGY_PER_BOOSTER_CARD);
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

	public static boolean hasInfiniteRange(@Nonnull ItemStack wirelessTerm) {
		if (LibConfig.USE_OLD_INFINTY_MECHANIC) {
			return isBoosterInstalled(wirelessTerm) || isWTCreative(wirelessTerm);
		}
		else {
			return hasInfinityEnergy(wirelessTerm);
		}
	}

	public static boolean hasInfinityEnergy(@Nonnull ItemStack wirelessTerm) {
		if (ensureTag(wirelessTerm).hasKey(INFINITY_ENERGY_NBT)) {
			return getInfinityEnergy(wirelessTerm) > 0 && LibConfig.WT_BOOSTER_ENABLED;
		}
		return isWTCreative(wirelessTerm);
	}

	public static boolean isAnyWT(@Nonnull ItemStack wirelessTerm) {
		return wirelessTerm.getItem() instanceof ICustomWirelessTerminalItem;
	}

	public static boolean isInRangeOfWAP(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player) {
		return getDistanceToWAP(wirelessTerm, player) <= getWAPRange(wirelessTerm, player) && getWAPRange(wirelessTerm, player) != Double.MAX_VALUE;
	}

	public static double getDistanceToWAP(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player) {
		IWirelessAccessPoint wap = getClosestWAPToPlayer(wirelessTerm, player);
		if (wap != null && player.getEntityWorld().provider.getDimension() == wap.getLocation().getWorld().provider.getDimension()) {
			BlockPos wapPos = wap.getLocation().getPos();
			BlockPos playerPos = player.getPosition();
			double distanceToWap = Math.sqrt(playerPos.distanceSq(wapPos));
			return distanceToWap;
		}
		return Double.MAX_VALUE;
	}

	public static double getWAPRange(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player) {
		IWirelessAccessPoint wap = getClosestWAPToPlayer(wirelessTerm, player);
		if (wap != null) {
			return wap.getRange();
		}
		return Double.MAX_VALUE;
	}

	public static IWirelessAccessPoint getClosestWAPToPlayer(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player) {
		List<IWirelessAccessPoint> wapList = getWAPs(wirelessTerm, player);
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

	public static List<IWirelessAccessPoint> getWAPs(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player) {
		if (isAnyWT(wirelessTerm)) {
			WTGuiObject<?, ?> object = getGUIObject(wirelessTerm, player);
			if (object != null) {
				return object.getWAPs();
			}
		}
		return Collections.emptyList();
	}

	public static WTGuiObject<?, ?> getGUIObject(EntityPlayer player) {
		return getGUIObject(null, player);
	}

	public static WTGuiObject<?, ?> getGUIObject(@Nullable ItemStack wirelessTerm, @Nonnull EntityPlayer player) {
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
					return new WTGuiObject<>(wth, wirelessTerm, player, player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
				}
			}
		}
		return null;
	}

	public static void setInfinityEnergy(@Nonnull ItemStack wirelessTerm, int amount) {
		if (!isWTCreative(wirelessTerm)) {
			NBTTagCompound nbt = ensureTag(wirelessTerm);
			nbt.setInteger(INFINITY_ENERGY_NBT, amount);
			wirelessTerm.setTagCompound(nbt);
		}
	}

	public static int getInfinityEnergy(@Nonnull ItemStack wirelessTerm) {
		NBTTagCompound nbt = ensureTag(wirelessTerm);
		if (!nbt.hasKey(INFINITY_ENERGY_NBT) && !isWTCreative(wirelessTerm)) {
			nbt.setInteger(INFINITY_ENERGY_NBT, 0);
		}
		return isWTCreative(wirelessTerm) ? Integer.MAX_VALUE : nbt.getInteger(INFINITY_ENERGY_NBT);
	}

	public static void drainInfinityEnergy(@Nonnull ItemStack wirelessTerm, EntityPlayer player, boolean isBauble, int slot) {
		if (player instanceof EntityPlayerMP) {
			if (!LibConfig.USE_OLD_INFINTY_MECHANIC && !isWTCreative(wirelessTerm)) {
				int current = getInfinityEnergy(wirelessTerm);
				if (!isInRangeOfWAP(wirelessTerm, player)) {
					int reducedAmount = current - INFINITY_ENERGY_DRAIN;
					if (reducedAmount < 0) {
						reducedAmount = 0;
					}
					setInfinityEnergy(wirelessTerm, reducedAmount);
					LibNetworking.instance().sendTo(new PacketSyncInfinityEnergyInv(getInfinityEnergy(wirelessTerm), player.getUniqueID(), isBauble, slot), (EntityPlayerMP) player);
				}
			}
		}
	}

	public static boolean isWTCreative(ItemStack wirelessTerm) {
		return !wirelessTerm.isEmpty() && wirelessTerm.getItem() instanceof ICustomWirelessTerminalItem && ((ICustomWirelessTerminalItem) wirelessTerm.getItem()).isCreative();
	}

	public static NBTTagCompound ensureTag(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound();
	}

	@SideOnly(Side.CLIENT)
	public static String color(String color) {
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

	@SideOnly(Side.CLIENT)
	public static EntityPlayer player() {
		return Minecraft.getMinecraft().player;
	}

	public static EntityPlayer player(InventoryPlayer playerInv) {
		return playerInv.player;
	}

	@SideOnly(Side.CLIENT)
	public static World world() {
		return Minecraft.getMinecraft().world;
	}

	public static World world(EntityPlayer player) {
		return player.getEntityWorld();
	}

	public static void chatMessage(EntityPlayer player, ITextComponent message) {
		player.sendMessage(message);
	}

}
