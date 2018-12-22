package p455w0rd.ae2wtlib.api;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import appeng.api.implementations.tiles.IWirelessAccessPoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.helpers.WTGuiObject;
import p455w0rd.ae2wtlib.items.ItemInfinityBooster;

public abstract class WTApi {

	protected static WTApi api = null;

	@Nullable
	public static WTApi instance() {
		if (WTApi.api == null) {
			try {
				Class<?> clazz = Class.forName("p455w0rd.ae2wtlib.init.LibApiImpl");
				Method instanceAccessor = clazz.getMethod("instance");
				WTApi.api = (WTApi) instanceAccessor.invoke(null);
			}
			catch (Throwable e) {
				return null;
			}
		}

		return WTApi.api;
	}

	public abstract WTConfig getConfig();

	public abstract WTRegistry getRegistry();

	public abstract WTNetworkHandler getNetHandler();

	public abstract ItemInfinityBooster getBoosterCard();

	public abstract List<Pair<Integer, ItemStack>> getWirelessTerminals(EntityPlayer player);

	public abstract List<Pair<Integer, ItemStack>> getWirelessTerminals(EntityPlayer player, boolean isBauble);

	// Parent pair contains a boolean which tells whether or not this is a bauble slot
	// Child pair gives the slot number and ItemStack
	public abstract List<Pair<Boolean, Pair<Integer, ItemStack>>> getAllWirelessTerminals(EntityPlayer player);

	// get a specific type of wireless terminal
	public abstract List<Pair<Boolean, Pair<Integer, ItemStack>>> getAllWirelessTerminalsByType(EntityPlayer player, Class<? extends ICustomWirelessTerminalItem> type);

	// used in cases where following method takes 'false' value for isBauble
	public abstract ItemStack getWTBySlot(EntityPlayer player, int slot, Class<? extends ICustomWirelessTerminalItem> type);

	public abstract ItemStack getWTBySlot(EntityPlayer player, boolean isBauble, int slot, Class<? extends ICustomWirelessTerminalItem> type);

	public abstract ItemStack getWTBySlot(EntityPlayer player, int slot);

	public abstract ItemStack getFirstWirelessTerminal(EntityPlayer player);

	public abstract boolean shouldConsumeBoosters(@Nonnull ItemStack wirelessTerminal);

	public abstract boolean isBoosterInstalled(@Nonnull final ItemStack wirelessTerminal);

	public abstract void setInRange(@Nonnull ItemStack wirelessTerm, boolean value);

	public abstract boolean isInRange(@Nonnull ItemStack wirelessTerm);

	public abstract ItemStack addInfinityBoosters(@Nonnull ItemStack wirelessTerm, @Nonnull ItemStack boosterCardStack);

	public abstract boolean hasInfiniteRange(@Nonnull ItemStack wirelessTerm);

	public abstract boolean hasInfinityEnergy(@Nonnull ItemStack wirelessTerm);

	// Is the ItemStack a Wireless Terminal registered through this library?
	public abstract boolean isAnyWT(@Nonnull ItemStack wirelessTerm);

	// Is the EntityPlayer holding the Wireless Terminal in range of a valid AE2 Wireless Access Point
	public abstract boolean isInRangeOfWAP(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player);

	// Get the distance to the nearest valid AE2 Wireless Access Point
	public abstract double getDistanceToWAP(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player);

	// Get the range (including booster cards) the the nearest AE2 Wireless Access Point covers
	public abstract double getWAPRange(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player);

	public abstract IWirelessAccessPoint getClosestWAPToPlayer(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player);

	// Get a list of WAPs on the linked network
	public abstract List<IWirelessAccessPoint> getWAPs(@Nonnull ItemStack wirelessTerm, @Nonnull EntityPlayer player);

	public abstract WTGuiObject<?, ?> getGUIObject(EntityPlayer player);

	public abstract WTGuiObject<?, ?> getGUIObject(@Nullable ItemStack wirelessTerm, @Nonnull EntityPlayer player);

	// Set Infinity Energy directly on a Wireless Terminal
	public abstract void setInfinityEnergy(@Nonnull ItemStack wirelessTerm, int amount);

	// Get a Wireless Terminals current Infinity Energy amount
	public abstract int getInfinityEnergy(@Nonnull ItemStack wirelessTerm);

	// Drains Infinity Energy from a Wireless Terminal (automatically syncs)
	public abstract void drainInfinityEnergy(@Nonnull ItemStack wirelessTerm, EntityPlayer player, boolean isBauble, int slot);

	// Is the Wireless Terminal a Creative version?
	public abstract boolean isWTCreative(ItemStack wirelessTerm);

	// Ensures the stack has a NBT Tag Compound
	public abstract NBTTagCompound ensureTag(ItemStack stack);

	// Gets text color via its string name (might remove)
	@SideOnly(Side.CLIENT)
	public abstract String color(String color);

	public static class Constants {

		public static class NBT {

			public static final String INFINITY_ENERGY_NBT = "InfinityEnergy";
			public static final String BOOSTER_SLOT_NBT = "BoosterSlot";
			public static final String IN_RANGE_NBT = "IsInRange";
			public static final String AUTOCONSUME_BOOSTER_NBT = "AutoConsumeBoosters";
			public static final String WT_ENCRYPTION_KEY = "encryptionKey";
			public static final String WT_INTERNAL_POWER = "internalCurrentPower";

		}

	}

}